import React, { useState, useEffect } from "react";
import { Table, message } from "antd";
import "./admin.css";
import Button from "../../components/Button";
import TheaterForm from "./theater-form";
import Shows from "./shows";
import { GetAllTheaters, DeleteTheater } from "../../apicalls/theaters";

function TheatersList() {
  const [showTheaterFormModal, setShowTheaterFormModal] = useState(false);
  const [selectedTheater, setSelectedTheater] = useState(null);
  const [formType, setFormType] = useState("add");
  const [Theaters, setTheaters] = useState([]);
  const [openShowsModal, setOpenShowsModal] = useState(false);

  const getData = async () => {
    try {
      const response = await GetAllTheaters();

      if (response.status == 200) {
        setTheaters(response.data);
      } else {
        message.error(response.message);
      }
    } catch (error) {
      message.error(error.message);
    }
  };

  const handleDelete = async (id) => {
    try {
      const response = await DeleteTheater(id);

      if (response.status == 200) {
        message.success(response.data);
        getData();
      } else {
        message.error(response.message);
      }
    } catch (error) {
      message.error(error.message);
    }
  };

  const columns = [
    {
      title: "Name",
      dataIndex: "name",
    },
    {
      title: "Address",
      dataIndex: "address",
    },
    {
      title: "Phone",
      dataIndex: "phone",
    },
    {
      title: "Email",
      dataIndex: "email",
    },
    // {
    //   title: "Status",
    //   dataIndex: "isActive",
    //   render: (text, record) => {
    //     if (text) {
    //       return "Approved";
    //     } else {
    //       return "Pending/ Blocked";
    //     }
    //   },
    // },
    {
      title: "Seating Capacity",
      dataIndex: "seatingCapacity",
    },
    {
      title: "Action",
      dataIndex: "action",
      render: (text, record) => {
        return (
          <div className="theater-icons">
            {/* <i
              className="ri-delete-bin-line"
              onClick={(e) => {
                e.stopPropagation();
                handleDelete(record.id); //id?
              }}
            ></i> */}
            <i
              className="ri-pencil-line"
              onClick={(e) => {
                e.stopPropagation();
                setFormType("edit");
                setSelectedTheater(record);
                setShowTheaterFormModal(true);
              }}
            ></i>
            <span
              className="underline"
              onClick={(e) => {
                e.stopPropagation();
                setSelectedTheater(record);
                setOpenShowsModal(true);
              }}
            >
              Shows
            </span>
          </div>
        );
      },
    },
  ];

  useEffect(() => {
    getData();
  }, []);

  return (
    <div>
      <div className="admin-movies-list">
        <Button
          title="Add Theater"
          variant="outlined"
          onClick={() => {
            setFormType("add");
            setShowTheaterFormModal(true);
          }}
        />
      </div>
      <Table columns={columns} dataSource={Theaters} />

      {showTheaterFormModal && (
        <TheaterForm
          showTheaterFormModal={showTheaterFormModal}
          setShowTheaterFormModal={setShowTheaterFormModal}
          formType={formType}
          selectedTheater={selectedTheater}
          setSelectedTheater={setSelectedTheater}
          getData={getData}
        />
      )}

      {openShowsModal && (
        <Shows
          openShowsModal={openShowsModal}
          setOpenShowsModal={setOpenShowsModal}
          theater={selectedTheater}
        />
      )}
    </div>
  );
}

export default TheatersList;
