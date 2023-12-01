import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import { Card, Button, Checkbox } from "antd";
import "./checkout.css";
import moment from "moment";
import { GetShowById, GetTheaterById } from "../../apicalls/theaters";
import { CreateBooking } from "../../apicalls/user";
import { useSelector } from "react-redux";
import { message } from "antd";
import { GetMovieById } from "../../apicalls/movies";
import Loader from "../../components/Loader/loader";

const Checkout = () => {
  const location = useLocation();
  const { userInfo } = useSelector((state) => state.auth);
  const params = new URLSearchParams(location.search);

  const [theaterDetails, setTheaterDetails] = useState({});
  const [showtimeDetails, setShowtimeDetails] = useState({});
  const [movieDetails, setMovieDetails] = useState({});

  const [useRewards, setUseRewards] = useState(false);
  const [rewardPoints, setRewardPoints] = useState(
    userInfo?.points ? userInfo.points : 0
  );
  const [discount, setDiscount] = useState(0);
  const [price, setPrice] = useState(0);
  const [totalPrice, setTotalPrice] = useState(0);
  const [finalDiscount, setFinalDiscount] = useState(0);
  const [minusDiscount, setMinusDiscount] = useState(0);
  const [usedRewardPoints, setUsedRewardPoints] = useState(0);
  const [isLoading, setIsLoading] = useState(false);

  const seats = parseInt(params.get("seats"), 8);

  const taxPerTicket = 1.5;
  const totalTax = seats * taxPerTicket;

  const fetchTheaterAndShowtimeDetails = async () => {
    setIsLoading(true);
    const theaterId = params.get("theaterId");
    const showtimeId = params.get("showtimeId");

    if (theaterId) {
      const theaterData = await GetTheaterById(theaterId);
      if (theaterData.status == 200) {
        setTheaterDetails(theaterData.data);
      }
    }

    if (showtimeId) {
      const showtimeData = await GetShowById(showtimeId);

      console.log(showtimeData);
      if (showtimeData.status == 200) {
        setShowtimeDetails(showtimeData.data);

        const movieData = await GetMovieById(showtimeData.data.movieId);

        if (movieData.status == 200) {
          setMovieDetails(movieData.data);
        }
      }
    }

    setIsLoading(false);
  };

  const handleRewardsChange = (e) => {
    setUseRewards(e.target.checked);
  };

  const handleBooking = async () => {
    let requestData;

    if (userInfo) {
      requestData = {
        showtimeId: showtimeDetails.id,
        userId: userInfo.userId,
        seatsBooked: seats,
        paymentMethod: "CREDIT_CARD",
        totalAmount: totalPrice,
        pointsAmount: 0,
        cashAmount: totalPrice,
        onlineServiceFee: totalTax,
      };

      if (useRewards) {
        requestData.paymentMethod = "POINTS";
        requestData.cashAmount = totalPrice - discount;
        requestData.pointsAmount = usedRewardPoints;
      }

      if (userInfo?.memberType === "PREMIUM") {
        requestData.onlineServiceFee = 0;
      }
    } else {
      requestData = {
        showtimeId: showtimeDetails.id,
        seatsBooked: seats,
        paymentMethod: "CREDIT_CARD",
        totalAmount: totalPrice,
        cashAmount: totalPrice,
        onlineServiceFee: totalTax,
      };
    }

    console.log(requestData);

    try {
      const response = await CreateBooking(requestData);
      console.log(response);

      if (response.status == 200) {
        message.success("Tickets Booked!");
      } else {
        message.error();
      }
    } catch (error) {
      console.error("Error making the payment:", error);
    }
  };

  useEffect(() => {
    window.scrollTo(0, 0);
    fetchTheaterAndShowtimeDetails();
  }, []);

  useEffect(() => {
    setPrice(seats * showtimeDetails.price);

    const priceIncludingTax = userInfo?.memberType === "PREMIUM" ? 0 : totalTax;

    if (showtimeDetails?.discountedPrice) {
      setTotalPrice(
        showtimeDetails.discountedPrice * seats + priceIncludingTax
      );
    } else {
      setTotalPrice(price + priceIncludingTax);
    }

    if (showtimeDetails?.discountedPrice) {
      const discount = showtimeDetails.discountedPrice * seats;
      setFinalDiscount(discount);
      setMinusDiscount(price - discount);
    }

    console.log(totalPrice);
  }, [showtimeDetails, theaterDetails, price, userInfo]);

  useEffect(() => {
    if (useRewards) {
      let pointsToDollars = rewardPoints / 10; // Convert points to dollar value
      let discountValue = Math.min(pointsToDollars, price); // Ensure discount doesn't exceed price
      setDiscount(discountValue);

      let usedPoints = discountValue * 10; // Convert discount value back to points
      setUsedRewardPoints(usedPoints); // Set used reward points
    } else {
      setDiscount(0);
      setUsedRewardPoints(0); // Reset used reward points if not using rewards
    }
  }, [showtimeDetails, useRewards, rewardPoints, price]);

  if (isLoading) {
    return <Loader />;
  }

  return (
    <div className="checkout-page-container">
      <div className="checkout-container mt-5">
        <h1>Checkout</h1>
        <div className="row">
          <div className="col-md-6">
            <img
              src={movieDetails?.posterUrl}
              alt="Movie Poster"
              className="img-fluid"
            />
          </div>
          <div className="col-md-6">
            <Card title="Ticket Details" bordered={false} className="card">
              <div className="order-summary">
                <div className="summary-item">
                  <p>
                    <strong>Theater:</strong>
                  </p>
                  <p>{theaterDetails.name || "Theater Name"}</p>
                </div>
                <div className="summary-item">
                  <p>
                    <strong>Address:</strong>
                  </p>
                  <p>{theaterDetails.address || "Theater Address"}</p>
                </div>
                <div className="summary-item">
                  <p>
                    <strong>Show Time:</strong>
                  </p>
                  {/* <p>
                    {moment(showtimeDetails.time).format("h:mm a") ||
                      "Show Time"}
                  </p> */}
                  <p>
                    {moment.utc(showtimeDetails.time).format("h:mm a") ||
                      "Show Time"}
                  </p>
                </div>
                <div className="summary-item">
                  <p>
                    <strong>Number of Seats:</strong>
                  </p>
                  <p>{seats}</p>
                </div>
                <div className="summary-item">
                  <p>
                    <strong>Price:</strong>
                  </p>
                  <p>${price.toFixed(2)}</p>
                </div>

                {showtimeDetails.discountedPrice && (
                  <div className="summary-item">
                    <p></p>
                    <p className="discount"> - ${minusDiscount.toFixed(2)}</p>
                  </div>
                )}
                {showtimeDetails.discountedPrice && (
                  <div className="summary-item">
                    <p>
                      <strong>Discounted Price:</strong>
                    </p>
                    <p>${finalDiscount.toFixed(2)}</p>
                  </div>
                )}

                {userInfo?.memberType != "PREMIUM" && (
                  <div className="summary-item">
                    <p>
                      <strong>Service Fee:</strong>
                    </p>
                    <p>${totalTax.toFixed(2)}</p>
                  </div>
                )}
                <hr />
                <div className="summary-total">
                  <p>
                    <strong>Total Price:</strong>
                  </p>
                  <p>${totalPrice.toFixed(2)}</p>
                </div>
                {userInfo && (
                  <div className="summary-item">
                    <p>
                      <strong>Your Reward Points:</strong>
                    </p>
                    <p>{rewardPoints}</p>
                  </div>
                )}
                {userInfo?.points > 0 && (
                  <div className="reward-points">
                    <Checkbox onChange={handleRewardsChange}>
                      Use Reward Points
                    </Checkbox>
                  </div>
                )}
                {useRewards && (
                  <div className="summary-item">
                    <p>
                      <strong>Discount:</strong>
                    </p>
                    <p>-${discount.toFixed(2)}</p>
                  </div>
                )}
                {useRewards && (
                  <div className="summary-item">
                    <p>
                      <strong>Used Reward Points:</strong>
                    </p>
                    <p>{usedRewardPoints}</p>
                  </div>
                )}
              </div>
              <Button
                type="primary"
                block
                className="proceed-button"
                onClick={handleBooking}
              >
                Proceed to Payment
              </Button>
              <div className="security-privacy">
                <i className="icon fas fa-lock"></i>
                <p>
                  Secure transaction. Your personal information is kept private.
                </p>
              </div>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Checkout;
