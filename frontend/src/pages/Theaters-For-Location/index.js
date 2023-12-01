import React, { useEffect, useState } from "react";
import CardGrid from "../../components/CardGrid";
import { useNavigate } from "react-router-dom";
import "./theaters.css";
import {
  GetLocationById,
  GetTheatersByLocation,
} from "../../apicalls/theaters";
import { useParams } from "react-router-dom";
import theaterPlaceholder from "../../assets/theater-placeholder.png";
import Loader from "../../components/Loader/loader";

const TheatersForLocation = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [locationInfo, setLocationInfo] = useState({});
  const [theaters, setTheaters] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchLocationAndTheaters = async () => {
    setIsLoading(true);
    try {
      const locationResponse = await GetLocationById(id);
      setLocationInfo(locationResponse.data);

      const theatersResponse = await GetTheatersByLocation(id);
      const formattedTheaters = theatersResponse.data.map((theater) => ({
        ...theater,
        title: theater.name,
        image: theater.posterUrl ? theater.posterUrl : theaterPlaceholder,
      }));

      setTheaters(formattedTheaters);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
    setIsLoading(false);
  };

  useEffect(() => {
    fetchLocationAndTheaters();
    window.scrollTo(0, 0);
  }, [id]);

  const handleCardClick = (theaterId) => {
    navigate(`/booking?theater=${theaterId}`);
  };

  if (isLoading) {
    return <Loader />;
  }

  return (
    <div className="homepage-container">
      <div className="theater-info">
        <h1>{locationInfo.name}</h1>
      </div>
      <h2>Theaters</h2>
      <div className="card-grid-container">
        {theaters.map((item, index) => (
          <div onClick={() => handleCardClick(item.id)} key={index}>
            <CardGrid
              title={item.title}
              description={item.description}
              image={item.image}
            />
          </div>
        ))}
      </div>
    </div>
  );
};

export default TheatersForLocation;
