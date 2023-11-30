package com.scrumandcoke.movietheaterclub.repository;

import com.scrumandcoke.movietheaterclub.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;


import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Integer> {
    Optional<MovieEntity> findByMovieName(String movieName);
    List<MovieEntity> findByReleaseDateAfter(Date date);
    List<MovieEntity> findByReleaseDateBetween(Date startDate, Date endDate);

    @Query("SELECT m FROM MovieEntity m WHERE m.multiplex.id = :multiplexId AND m.releaseDate >= :oneWeekAgo AND m.releaseDate <= :today")
    List<MovieEntity> findMoviesWithRecentRelease(@Param("multiplexId") Integer multiplexId, @Param("oneWeekAgo") Date oneWeekAgo, @Param("today") Date today);

}