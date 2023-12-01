import React, { useState, useEffect } from "react";
import { Table, message } from "antd";
import { GetAllMovies } from "../../apicalls/movies";
import moment from "moment";
import "./admin.css";

function MoviesList() {
  const [movies, setMovies] = useState([]);

  const getData = async () => {
    try {
      const response = await GetAllMovies();

      if (response.status == 200) {
        setMovies(response.data);
      } else {
        message.error(response.data);
      }
    } catch (error) {
      message.error(error);
    }
  };

  const columns = [
    {
      title: "Poster",
      dataIndex: "poster",
      render: (text, record) => {
        return (
          <img
            src={record.posterUrl}
            alt="poster"
            height="60"
            width="60"
            className="br-1"
          />
        );
      },
    },
    {
      title: "Name",
      dataIndex: "movieName",
    },
    {
      title: "Synopsis",
      dataIndex: "synopsis",
    },
    {
      title: "Duration",
      dataIndex: "runningTime",
      render: (text, record) => {
        let duration = moment.duration(record.runningTime, "minutes");
        let hours = duration.hours();
        let minutes = duration.minutes();
        return `${hours}h ${minutes}m`;
      },
    },
    {
      title: "Genre",
      dataIndex: "genre",
    },
    {
      title: "Language",
      dataIndex: "language",
    },
    {
      title: "Release Date",
      dataIndex: "releaseDate",
      render: (text, record) => {
        return moment(record.releaseDate).format("DD-MM-YYYY");
      },
    },
  ];

  useEffect(() => {
    getData();
  }, []);

  return (
    <div>
      <Table columns={columns} dataSource={movies} />
    </div>
  );
}

export default MoviesList;
