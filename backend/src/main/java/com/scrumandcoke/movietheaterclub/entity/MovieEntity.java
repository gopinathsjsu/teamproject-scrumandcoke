package com.scrumandcoke.movietheaterclub.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "movies")
@Data
public class MovieEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Integer movieId;

    @Column(name = "movie_name")
    private String movieName;

    private String synopsis;

    @Column(name = "running_time")
    private Integer runningTime;

    @Column(name="poster")
    private String posterUrl;

    @Column(name="genre")
    private String genre;

    @Column(name="language")
    private String language;

    @Column(name="release_date")
    private Date releaseDate;

    @ManyToOne
    @JoinColumn(name="multiplex_id")
    private MultiplexEntity multiplex;
}
