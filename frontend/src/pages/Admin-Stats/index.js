import React, { useState, useEffect } from "react";
import { Bar } from "react-chartjs-2";
import { Select } from "antd";
import "./admin-stats.css";
import { GetAnalytics1 } from "../../apicalls/user";
import { GetAllTheaters } from "../../apicalls/theaters";
import { GetAllMovies } from "../../apicalls/movies";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

const AnalyticsDashboard = () => {
  const [selectedPeriod, setSelectedPeriod] = useState(30);
  const [theaters, setTheaters] = useState([]);
  const [selectedTheater, setSelectedTheater] = useState(null);
  const [occupancyData, setOccupancyData] = useState([]);
  const [movies, setMovies] = useState([]);

  const totalSeats = 300;

  useEffect(() => {
    GetAllTheaters().then((data) => {
      if (data.status === 200) {
        setTheaters(data.data);
        setSelectedTheater(data.data[0]?.id);
      }
    });

    GetAllMovies().then((movieData) => {
      const movieMapping = {};

      movieData.data.forEach((movie) => {
        movieMapping[movie.movieId] = movie.movieName;
      });

      setMovies(movieMapping);
    });
  }, []);

  useEffect(() => {
    if (selectedTheater) {
      GetAnalytics1(selectedPeriod).then((data) => {
        if (data.status === 200) {
          const filteredData = data.data.filter(
            (item) => item.theaterId === selectedTheater
          );
          setOccupancyData(filteredData);
        }
      });
    }
  }, [selectedPeriod, selectedTheater]);

  const processDataForChart = () => {
    const occupancyByMovie = occupancyData.reduce((acc, cur) => {
      const movieName = movies[cur.movieId] || `Unknown (${cur.movieId})`;
      const occupancyKey = `occupancyLast${selectedPeriod}Days`;
      acc[movieName] = (acc[movieName] || 0) + cur[occupancyKey];
      return acc;
    }, {});

    return {
      labels: Object.keys(occupancyByMovie),
      datasets: [
        {
          label: "Occupancy",
          data: Object.values(occupancyByMovie),
          backgroundColor: "rgba(255, 99, 132, 0.5)",
        },
        {
          label: "Total Seats",
          data: Object.keys(occupancyByMovie).map(() => totalSeats),
          backgroundColor: "rgba(54, 162, 235, 0.5)",
        },
      ],
    };
  };

  const dataByMovie = processDataForChart();

  return (
    <div className="stats-container">
      <h1>Theater Occupancy Analytics</h1>

      <div className="selects">
        <Select
          value={selectedTheater}
          style={{ width: 120, marginLeft: "10px" }}
          onChange={(value) => setSelectedTheater(value)}
        >
          {theaters.map((theater) => (
            <Select.Option key={theater.id} value={theater.id}>
              {theater.name}
            </Select.Option>
          ))}
        </Select>
        <Select
          defaultValue={30}
          style={{ width: 120 }}
          onChange={(value) => setSelectedPeriod(value)}
        >
          <Select.Option value={30}>Last 30 Days</Select.Option>
          <Select.Option value={60}>Last 60 Days</Select.Option>
          <Select.Option value={90}>Last 90 Days</Select.Option>
        </Select>
      </div>

      <div className="chart-container">
        <h2>Occupancy by Movie</h2>
        <Bar data={dataByMovie} />
      </div>
    </div>
  );
};

export default AnalyticsDashboard;
