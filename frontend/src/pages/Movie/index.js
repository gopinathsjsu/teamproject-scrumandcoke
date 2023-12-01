import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Button } from "react-bootstrap";
import "./movie.css";
import { GetMovieById } from "../../apicalls/movies";
import { message } from "antd";
import moment from "moment";
import Loader from "../../components/Loader/loader";
import moviePlaceholder from "../../assets/movie-placeholder.png";

const Movie = () => {
  var navigate = useNavigate();
  const { id } = useParams();

  const [movieData, setMovieData] = useState({});
  const [isLoading, setIsLoading] = useState(false);

  const fetchMovieData = async () => {
    try {
      setIsLoading(true);
      const response = await GetMovieById(id);
      console.log(response);

      if (response.status == 200) {
        setMovieData(response.data);
      } else {
        message.error(response.response.data);
      }
      setIsLoading(false);
    } catch (error) {
      setIsLoading(false);
      message.error(error);
      console.log(error);
    }
  };

  useEffect(() => {
    window.scrollTo(0, 0);
    fetchMovieData();
  }, []);

  if (isLoading) {
    return <Loader />;
  }

  return (
    <div className="container-fluid movie">
      <div className="row movie-poster-row">
        <div className="col movie-poster-col">
          <div className="movie-det-section">
            <img
              src={movieData.posterUrl ? movieData.posterUrl : moviePlaceholder}
              alt="Movie not found"
              className="movie-poster"
            />
          </div>
        </div>
      </div>
      <div className="row movie-det-row">
        <div className="col-lg-4">
          <img
            src={movieData.posterUrl ? movieData.posterUrl : moviePlaceholder}
            alt="Movie not found"
            className="movie-img"
          />
        </div>
        <div className="col-lg movie-det-col">
          <div className="movie-title">{movieData?.movieName}</div>
          <div className="general-info">
            <span className="content-rating">{movieData?.language}</span>
            <span className="movie-genres">{movieData?.genre}</span>
            <span className="runtime ">
              {movieData?.runningTime &&
                `${moment.duration(movieData.runningTime, "minutes").hours()}h 
     ${moment.duration(movieData.runningTime, "minutes").minutes()}m`}
            </span>
          </div>
          {movieData?.synopsis != null ? (
            <div className="overview">
              <h3>Overview</h3>
              <span>{movieData?.synopsis}</span>
            </div>
          ) : (
            ""
          )}

          <div className="book-ticket-btn">
            <Button
              variant="primary"
              type="submit"
              className="book-tkt-btn"
              onClick={() => navigate(`/booking?movie=${id}`)}
            >
              Book Tickets
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Movie;
