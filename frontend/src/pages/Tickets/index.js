import React, { useEffect, useState } from "react";
import { Tabs, Card, Button } from "antd";
import "./tickets.css";
import {
  GetUpcomingBookings,
  GetPastBookings,
  CancelBooking,
  GetCancelledBookings,
} from "../../apicalls/user";
import { useSelector } from "react-redux";
import { message } from "antd";
import Loader from "../../components/Loader/loader";
import moment from "moment";

const { TabPane } = Tabs;

const ShowCard = ({ show, isUpcoming, cancelShow }) => (
  <Card className="show-card">
    <div className="card-content">
      <h3>{show.movieName}</h3>
      <p>Show Time: {formatTimeUTC(show.showTimeDate)}</p>
      <p>Date: {formatDateUTC(show.showTimeDate)}</p>
      <p>Tickets: {show.bookingDto.seatsBooked}</p>
    </div>
    {isUpcoming && (
      <div className="card-action">
        <Button type="primary" onClick={() => cancelShow(show.bookingDto.bookingId)}>
          Cancel
        </Button>
      </div>
    )}

    { show.bookingDto.bookingStatus==='CANCELLED' && (
      <Button type="primary" disabled>
          Cancelled
        </Button>
    )}
  </Card>
);

const formatDateUTC = (date) => {
  return moment.utc(date).format("MMMM Do YYYY");
};

const formatTimeUTC = (date) => {
  return moment.utc(date).format("HH:mm");
};

const Shows = () => {
  const [activeTab, setActiveTab] = useState("upcoming");
  const [user, setUser] = useState({
    points: 1200,
    isPremium: true,
    pointsExpiring: 200,
  });

  const { userInfo } = useSelector((state) => state.auth);

  const [pastShows, setPastShows] = useState([]);
  const [upcomingShows, setUpcomingShows] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [cancelled, setCancelled] = useState(0);

  const getUpcomingBookings = async () => {
    try {
      setIsLoading(true);
      const response = await GetUpcomingBookings(userInfo.userId);

      if (response.status === 200) {
        setUpcomingShows(response.data);
      } else {
        message.error(response?.data);
      }
      setIsLoading(false);
    } catch (error) {
      setIsLoading(false);
      message.error(error?.message);
    }
  };

  const getPastBookings = async () => {
    try {
      setIsLoading(true);
      const response = await GetPastBookings(userInfo.userId);

      if (response.status === 200) {
        setPastShows(response.data);
      } else {
        message.error(response?.data);
      }
      setIsLoading(false);
    } catch (error) {
      setIsLoading(false);
      message.error(error?.message);
    }
  };

  const getCancelledBookings = async () => {
    try {
      setIsLoading(true);
      const response = await GetCancelledBookings(userInfo.userId);

      if (response.status === 200) {
        pastShows.concat(response.data)
        setPastShows(response.data);
      } else {
        message.error(response?.data);
      }
      setIsLoading(false);
    } catch (error) {
      setIsLoading(false);
      message.error(error?.message);
    }
  };

  const Sidebar = () => {
    return (
      <div className="sidebar">
        <h3>User Points: {userInfo.points ? userInfo.points : 0}</h3>
        <p>
          Status:{" "}
          <strong>
            {userInfo.memberType === "PREMIUM" ? "Premium" : "Regular"} Member
          </strong>
        </p>
        <p>
          Equivalent Cash:{" "}
          <strong>
            $
            {!userInfo.points || userInfo.points === 0
              ? 0
              : userInfo.points / 10}
          </strong>
        </p>
      </div>
    );
  };

  const cancelShow = async (id) => {
    try {
      const response = await CancelBooking(id);

      if (response.status === 200) {
        setUpcomingShows(upcomingShows.filter((show) => show.id !== id));
        message.success("Booking cancelled successfully");
        setCancelled(cancelled+1);
      } else {
        message.error("Failed to cancel the booking");
      }
    } catch (error) {
      message.error(
        error.message || "An error occurred while cancelling the booking"
      );
    }
  };

  useEffect(() => {
    getPastBookings();
    getUpcomingBookings();
    getCancelledBookings();
  }, [cancelled]);

  if (isLoading) {
    return <Loader />;
  }

  return (
    <div className="page-container">
      <Tabs
        defaultActiveKey="upcoming"
        onChange={setActiveTab}
        className="shows-tabs"
      >
        <TabPane tab="Upcoming Shows" key="upcoming">
          <div className="show-cards">
            {upcomingShows.map((show) => (
              <ShowCard
                key={show.id}
                show={show}
                isUpcoming={true}
                cancelShow={cancelShow}
              />
            ))}
          </div>
        </TabPane>
        <TabPane tab="Past Shows" key="past">
          <div className="show-cards">
            {pastShows.map((show) => (
              <ShowCard key={show.id} show={show} isUpcoming={false} />
            ))}
          </div>
        </TabPane>
      </Tabs>
      <Sidebar user={user} />
    </div>
  );
};

export default Shows;
