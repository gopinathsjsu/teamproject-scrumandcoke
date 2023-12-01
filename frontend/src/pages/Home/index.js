import React, { useState, useEffect } from "react";
import { Carousel, message } from "antd";
import CardGrid from "../../components/CardGrid";
import { useNavigate } from "react-router-dom";
import "./home.css";
import {
  GetAllMovies,
  GetCurrentlyPlaying,
  GetUpcomingShows,
} from "../../apicalls/movies";
import { GetAllLocations, GetAllTheaters } from "../../apicalls/theaters";
import Loader from "../../components/Loader/loader";
import moviePlaceholder from "../../assets/movie-placeholder.png";
import theaterPlaceholder from "../../assets/theater-placeholder.png";
import locationPlaceholder from "../../assets/place-placeholder.png";

const HomePage = () => {
  const navigate = useNavigate();

  const [isLoading, setIsLoading] = useState(false);
  const [movies, setMovies] = useState([]);

  const [theaters, setTheaters] = useState([]);

  const [locations, setLocations] = useState([]);

  const [upcomingMovies, setUpcomingMovies] = useState([]);

  const getMoviesData = async () => {
    try {
      setIsLoading(true);
      const response = await GetCurrentlyPlaying();

      if (response.status === 200) {
        const formattedMovies = response.data.map((movie) => ({
          ...movie,
          id: movie.movieId,
          title: movie.movieName,
          description: movie.synopsis,
          image: movie.posterUrl ? movie.posterUrl : moviePlaceholder,
        }));
        setMovies(formattedMovies);
      } else {
        message.error(response.data);
      }
      setIsLoading(false);
    } catch (error) {
      setIsLoading(false);
      message.error(error);
    }
  };

  const getUpcomingShows = async () => {
    try {
      setIsLoading(true);
      const response = await GetUpcomingShows();
      console.log(response);

      if (response.status === 200) {
        const formattedMovies = response.data.map((movie) => ({
          ...movie,
          id: movie.movieId,
          title: movie.movieName,
          description: movie.synopsis,
          image: movie.posterUrl ? movie.posterUrl : moviePlaceholder,
        }));
        setUpcomingMovies(formattedMovies);
      } else {
        message.error(response?.data);
      }
      setIsLoading(false);
    } catch (error) {
      setIsLoading(false);
      message.error(error);
    }
  };

  const getTheatersData = async () => {
    try {
      setIsLoading(true);
      const response = await GetAllTheaters();

      if (response.status === 200) {
        const formattedTheaters = response.data.map((theater) => ({
          ...theater,
          title: theater.name,
          image: theater.posterUrl ? theater.posterUrl : theaterPlaceholder,
        }));
        setTheaters(formattedTheaters);
      } else {
        message.error(response.data);
      }
      setIsLoading(false);
    } catch (error) {
      setIsLoading(false);
      message.error(error.message);
    }
  };

  const getLocations = async () => {
    try {
      setIsLoading(true);
      const response = await GetAllLocations();

      if (response.status === 200) {
        const formattedLocations = response.data.map((location) => ({
          ...location,
          title: location.name,
          image: location.posterUrl ? location.posterUrl : locationPlaceholder,
        }));
        setLocations(formattedLocations);
      } else {
        message.error(response.data);
      }
      setIsLoading(false);
    } catch (error) {
      setIsLoading(false);
      message.error(error.message);
    }
  };

  const CustomNextArrow = ({ onClick }) => (
    <div className="custom-next-arrow" onClick={onClick}>
      →
    </div>
  );

  const CustomPrevArrow = ({ onClick }) => (
    <div className="custom-prev-arrow" onClick={onClick}>
      ←
    </div>
  );

  const handleCardClick = (type, id) => {
    navigate(`/${type}/${id}`);
  };

  const renderCarousel = (data, type) => {
    return (
      <Carousel
        slidesToShow={4}
        dots={false}
        swipeToSlide={true}
        draggable={true}
        infinite={true}
        arrows={true}
        nextArrow={<CustomNextArrow />}
        prevArrow={<CustomPrevArrow />}
      >
        {data.map((item) => (
          <div className="card-container">
            <CardGrid
              key={item.id}
              de
              title={item.title}
              description={item.description}
              image={item.image}
              onClick={() => handleCardClick(type, item.id)}
            />
          </div>
        ))}
      </Carousel>
    );
  };

  useEffect(() => {
    getMoviesData();
    getTheatersData();
    getUpcomingShows();
    getLocations();
  }, []);

  if (isLoading) {
    return <Loader />;
  }

  return (
    <div className="homepage-container">
      <h2 className="h2-homepage">Theaters</h2>
      {renderCarousel(theaters, "theater")}

      <h2 className="h2-homepage">Locations</h2>
      {renderCarousel(locations, "location")}

      <h2 className="h2-homepage">Currently Playing</h2>
      {renderCarousel(movies, "movie")}

      <h2 className="h2-homepage">Upcoming Movies</h2>
      {renderCarousel(upcomingMovies, "movie")}
    </div>
  );
};

export default HomePage;
