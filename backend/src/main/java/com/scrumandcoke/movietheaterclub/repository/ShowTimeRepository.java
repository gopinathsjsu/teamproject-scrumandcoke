package com.scrumandcoke.movietheaterclub.repository;

import com.scrumandcoke.movietheaterclub.entity.ShowTimeEntity;
import com.scrumandcoke.movietheaterclub.enums.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTimeEntity, Integer> {
    List<ShowTimeEntity> findByMovie_MovieId(Integer id);

    List<ShowTimeEntity> findByTheaterScreen_Id(Integer id);

    List<ShowTimeEntity> findByTheaterScreen_IdAndTheaterScreen_MultiplexEntity_Id(int theaterScreen_id, int multiplex_id);

    List<ShowTimeEntity> findByTheaterScreen_MultiplexEntity_Location(Location location);

}
