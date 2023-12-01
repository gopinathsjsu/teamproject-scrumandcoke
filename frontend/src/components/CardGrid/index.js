import React from "react";
import { Card as AntCard } from "antd";

const CardGrid = ({ title, description, image, onClick }) => {
  const truncatedDescription =
    description?.length > 50 ? `${description?.slice(0, 50)}...` : description;

  return (
    <AntCard
      hoverable
      style={{ width: "240px", margin: "10px" }}
      cover={
        <img
          alt={title}
          src={image}
          style={{ height: "325px", objectFit: "cover" }}
        />
      }
      onClick={onClick}
    >
      <AntCard.Meta title={title} description={truncatedDescription} />
    </AntCard>
  );
};

export default CardGrid;
