import { Col, Form, Modal, Row, Table, message } from "antd";
import React, { useEffect, useState } from "react";
import Button from "../../components/Button";
import "./admin.css";
import { GetAllMovies, GetMovieById } from "../../apicalls/movies";
import {
  AddShow,
  DeleteShow,
  GetShowsByTheater,
  UpdateShow,
} from "../../apicalls/theaters";
import moment from "moment";

function Shows({ openShowsModal, setOpenShowsModal, theater }) {
  const [view, setView] = useState("table");
  const [shows, setShows] = useState([]);
  const [movies, setMovies] = useState([]);
  const [editingShow, setEditingShow] = useState(null);
  const [showDiscountedPrice, setShowDiscountedPrice] = useState(false);
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedTime, setSelectedTime] = useState(null);

  const fetchMovieNameById = async (movieId) => {
    try {
      const response = await GetMovieById(movieId);
      return response.status == 200 ? response.data.movieName : "Unknown";
    } catch (error) {
      console.error("Error fetching movie details:", error);
      return "Unknown";
    }
  };

  const getData = async () => {
    try {
      const moviesResponse = await GetAllMovies();

      if (moviesResponse.status == 200) {
        setMovies(moviesResponse.data);
      } else {
        message.error(moviesResponse.message);
      }

      const showsResponse = await GetShowsByTheater(theater.id);
      //console.log(showsResponse);
      if (showsResponse.status == 200) {
        const showsWithMoviesAndDateTime = await Promise.all(
          showsResponse.data.map(async (show) => {
            const movieName = await fetchMovieNameById(show.movieId);

            const showDateTime = moment(show.time);
            const date = showDateTime.format("YYYY-MM-DD");
            const time = showDateTime.format("HH:mm:ss");

            return {
              id: show.id,
              price: show.price,
              movie: movieName,
              date,
              time,
              movieId: show.movieId,
              discountedPrice: show.discountedPrice,
              availableSeats: show.availableSeats,
            };
          })
        );

        setShows(showsWithMoviesAndDateTime);
        //console.log(showsWithMoviesAndDateTime);
      } else {
        message.error(showsResponse.message);
      }
    } catch (error) {
      message.error(error.message);
    }
  };

  const handleDateTimeChange = (date, time) => {
    if (!date || !time) {
      setShowDiscountedPrice(false);
      return;
    }

    const selectedDateTime = moment.utc(`${date} ${time}`, "YYYY-MM-DD HH:mm");

    if (selectedDateTime.day() === 2 && selectedDateTime.hour() < 18) {
      setShowDiscountedPrice(true);
    } else {
      setShowDiscountedPrice(false);
    }
  };

  const handleAddShow = async (data) => {
    const dateTime = `${data.date} ${data.time}`;
    const formattedDateTime = moment
      .utc(dateTime, "YYYY-MM-DD HH:mm")
      .toISOString();

    const finalData = {
      movieId: data.movieId,
      theaterScreenId: theater.id,
      price: data.price,
      time: formattedDateTime,
      discountedPrice: data.discountedPrice,
    };

    try {
      const response = await AddShow(finalData);

      if (response.status == 200) {
        message.success("Show Added Successfully!");
        getData();
        setView("table");
      } else {
        message.error(response?.data);
      }
    } catch (error) {
      message.error(error?.message);
    }
  };

  const handleDelete = async (id) => {
    try {
      const response = await DeleteShow(id);

      if (response.status == 200) {
        message.success("Show Deleted Successfully!");
        getData();
      } else {
        message.error(response?.message);
      }
    } catch (error) {
      message.error(error.message);
    }
  };

  const editShow = (record) => {
    setEditingShow(record);
    console.log(record);
    setSelectedDate(record.date);
    setSelectedTime(record.time);

    setView("form");
  };

  const onEdit = async (formValues) => {
    const formattedDateTime =
      formValues.date + "T" + formValues.time + ".000+00:00";

    const updatedData = {
      id: editingShow.id,
      movieId: Number(formValues.movieId),
      theaterScreenId: theater.id,
      price: Number(formValues.price),
      time: formattedDateTime,
    };

    try {
      const response = await UpdateShow(updatedData);

      if (response.status == 200) {
        message.success("Show Updated Successfully!");
        getData();
      } else {
        message.error(response?.message);
      }
    } catch (error) {
      message.error("Error updating show: " + error.message);
    }

    setView("table");
    setEditingShow(null);
  };

  const onFinish = (values) => {
    if (editingShow) {
      onEdit(values);
    } else {
      handleAddShow(values);
    }
  };

  const columns = [
    // {
    //   title: "Show Name",
    //   dataIndex: "name",
    // },
    {
      title: "Date",
      dataIndex: "date",
      render: (text, record) => {
        return moment(text).format("MMM Do YYYY");
      },
    },
    {
      title: "Time",
      dataIndex: "time",
    },
    {
      title: "Movie",
      dataIndex: "movie",
    },
    {
      title: "Ticket Price",
      dataIndex: "price",
    },
    // {
    //   title: "Total Seats",
    //   dataIndex: "totalSeats",
    // },
    {
      title: "Available Seats",
      dataIndex: "availableSeats",
    },
    {
      title: "Action",
      dataIndex: "action",
      render: (text, record) => (
        <div className="theater-icons">
          <i
            className="ri-delete-bin-line"
            onClick={() => {
              handleDelete(record.id);
            }}
          ></i>
          <i className="ri-pencil-line" onClick={() => editShow(record)}></i>
        </div>
      ),
    },
  ];

  useEffect(() => {
    getData();
  }, []);

  useEffect(() => {
    handleDateTimeChange(selectedDate, selectedTime);
  }, [selectedDate, selectedTime]);

  return (
    <Modal
      title=""
      open={openShowsModal}
      onCancel={() => setOpenShowsModal(false)}
      width={1400}
      footer={null}
    >
      <h1 className="shows-heading">Theater : {theater?.name}</h1>
      <hr />

      <div className="shows-subheading">
        <h1>{view === "table" ? "Shows" : "Add Show"}</h1>
        {view === "table" && (
          <Button
            variant="outlined"
            title="Add Show"
            onClick={() => {
              setView("form");
            }}
          />
        )}
      </div>

      {view === "table" && <Table columns={columns} dataSource={shows} />}

      {view === "form" && (
        <Form
          layout="vertical"
          onFinish={onFinish}
          initialValues={editingShow ? { ...editingShow } : {}}
        >
          <Row gutter={[16, 16]}>
            <Col span={8}>
              <Form.Item
                label="Date"
                name="date"
                rules={[{ required: true, message: "Please input show date!" }]}
              >
                <input
                  type="date"
                  min={new Date().toISOString().split("T")[0]}
                  onChange={(e) => setSelectedDate(e.target.value)}
                />
              </Form.Item>
            </Col>

            <Col span={8}>
              <Form.Item
                label="Time"
                name="time"
                rules={[{ required: true, message: "Please input show time!" }]}
              >
                <input
                  type="time"
                  onChange={(e) => setSelectedTime(e.target.value)}
                />
              </Form.Item>
            </Col>

            <Col span={8}>
              <Form.Item
                label="Movie"
                name="movieId"
                rules={[{ required: true, message: "Please select movie!" }]}
              >
                <select>
                  <option value="">Select Movie</option>
                  {movies.map((movie) => (
                    <option value={movie.movieId}>{movie.movieName}</option>
                  ))}
                </select>
              </Form.Item>
            </Col>

            <Col span={8}>
              {/* <div className="ticket-price-buttons">
                <label>Ticket Price</label>
                <button
                  type="button"
                  className={`price-button original-price ${
                    !isDiscounted ? "selected-price" : ""
                  }`}
                  onClick={() => handlePriceSet(false)}
                >
                  Original Price (${priceOptions.originalPrice})
                </button>
                <button
                  type="button"
                  className={`price-button discounted-price ${
                    isDiscounted ? "selected-price" : ""
                  }`}
                  onClick={() => handlePriceSet(true)}
                >
                  Discounted Price (${priceOptions.discountedPrice})
                </button>
              </div> */}
              <Form.Item
                label="Price"
                name="price"
                rules={[{ required: true }]}
              >
                <input type="number" />
              </Form.Item>
            </Col>

            {showDiscountedPrice && (
              <Col span={8}>
                <Form.Item label="Discounted Price" name="discountedPrice">
                  <input type="number" />
                </Form.Item>
              </Col>
            )}

            {/* <Col span={8}>
              <Form.Item
                label="Total Seats"
                name="totalSeats"
                rules={[{ message: "Please input total seats!" }]}
              >
                <input type="number" />
              </Form.Item>
            </Col> */}
          </Row>
          <div className="shows-button">
            <Button
              variant="outlined"
              title="Cancel"
              onClick={() => {
                setView("table");
                setEditingShow(null);
              }}
            />
            <Button variant="contained" title="SAVE" type="submit" />
          </div>
        </Form>
      )}
    </Modal>
  );
}

export default Shows;
