import React, { useEffect, useState } from "react";
import CardGrid from "../../components/CardGrid";
import { useNavigate, useParams } from "react-router-dom";
import "./movies.css";
import { GetMoviesByTheater, GetTheaterById } from "../../apicalls/theaters";
import { message } from "antd";
import moviePlaceholder from "../../assets/movie-placeholder.png";
import Loader from "../../components/Loader/loader";

const MoviesForTheater = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [theater, setTheater] = useState({});
  const [isLoading, setIsLoading] = useState(false);

  const [movies, setMovies] = useState([]);

  const fetchTheaterData = async () => {
    try {
      setIsLoading(true);
      const response = await GetTheaterById(id);
      console.log(response);

      if (response.status == 200) {
        setTheater(response.data);
      } else {
        message.error(response?.data);
      }
      setIsLoading(false);
    } catch (error) {
      setIsLoading(false);
      message.error(error);
    }
  };

  const fetchMovies = async () => {
    setIsLoading(true);
    try {
      const response = await GetMoviesByTheater(id);

      if (response.status == 200) {
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

  const handleCardClick = (theaterId, movieId) => {
    navigate(`/booking?theater=${theaterId}&movie=${movieId}`);
  };

  useEffect(() => {
    window.scrollTo(0, 0);
    fetchTheaterData();
    fetchMovies();
  }, []);

  if (isLoading) {
    return <Loader />;
  }

  return (
    <div className="homepage-container">
      <div className="theater-info">
        <h1>{theater?.name}</h1>
        <p>{theater?.location}</p>
        {/* Other theater information */}
      </div>
      <h2>Now Showing</h2>
      <div className="card-grid-container">
        {movies.map((item, index) => (
          <div
            onClick={() => handleCardClick(id, item.id)} // Assuming theater ID is 1 for now
            key={index}
          >
            <CardGrid
              title={item.title}
              description={item.description}
              image={item.posterUrl}
            />
          </div>
        ))}
      </div>
    </div>
  );
};

export default MoviesForTheater;
