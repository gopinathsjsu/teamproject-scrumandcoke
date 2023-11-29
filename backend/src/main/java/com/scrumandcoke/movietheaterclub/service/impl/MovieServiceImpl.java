package com.scrumandcoke.movietheaterclub.service.impl;

import com.scrumandcoke.movietheaterclub.dto.MovieDto;
import com.scrumandcoke.movietheaterclub.exception.GlobalException;
import com.scrumandcoke.movietheaterclub.model.MovieEntity;
import com.scrumandcoke.movietheaterclub.repository.MovieRepository;
import com.scrumandcoke.movietheaterclub.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    MovieRepository movieRepository;

    Logger logger = LoggerFactory.getLogger(MovieServiceImpl.class);

    @Override
    public void addMovie(MovieDto movieDto) throws GlobalException {
        try {
            movieRepository.save(MovieDto.toEntity(movieDto));
        } catch (Exception exception) {
            logger.error("Error saving movie: {}", movieDto.getMovieName());
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public MovieDto getMovie(Integer movieId) throws GlobalException {
        try {
            MovieEntity movieEntity = movieRepository.findById(movieId).get();
            return MovieDto.fromEntity(movieEntity);
        } catch (Exception exception) {
            logger.error("Error getting movie: {}", movieId);
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<MovieDto> getMovies() throws GlobalException {
        try {
            List<MovieEntity> movieEntities = movieRepository.findAll();
            return MovieDto.fromEntityList(movieEntities);
        } catch (Exception exception) {
            logger.error("Error getting movies");
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public void updateMovie(MovieDto movieDto) throws GlobalException {
        try {
            MovieEntity movieEntity = movieRepository.findById(movieDto.getMovieId()).get();
            movieEntity.setMovieName(movieDto.getMovieName());
            movieEntity.setSynopsis(movieDto.getSynopsis());
            movieEntity.setRunningTime(movieDto.getRunningTime());

            movieEntity.setGenre(movieDto.getGenre());
            movieEntity.setLanguage(movieDto.getLanguage());
            movieEntity.setReleaseDate(movieDto.getReleaseDate());

            movieEntity.setPoster(movieDto.getPoster());

            movieRepository.save(movieEntity);

        } catch (Exception exception) {
            logger.error("Error updating movie: {}", movieDto.getMovieName());
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deleteMovie(Integer id) throws GlobalException {
        try {
            movieRepository.deleteById(id);
        } catch (Exception exception) {
            logger.error("Error deleting movie: {}", id);
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<MovieDto> getUpcomingMovies() throws GlobalException {
        try {
            // Get the current date with time set to the start of the day
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.clear(Calendar.MINUTE);
            calendar.clear(Calendar.SECOND);
            calendar.clear(Calendar.MILLISECOND);

            Date today = calendar.getTime();

            // Convert Date to LocalDate
            LocalDate localDateToday = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            logger.info("Current date (set to midnight): {}", sdf.format(today));

            // Use LocalDate in findByReleaseDateAfter
            List<MovieEntity> upcomingMovies = movieRepository.findByReleaseDateAfter(localDateToday);
            return MovieDto.fromEntityList(upcomingMovies);
        } catch (Exception exception) {
            logger.error("Error getting upcoming movies", exception);
            throw new GlobalException(exception.getMessage(), exception);
        }
    }


    @Override
    public List<MovieDto> getCurrentMovies() throws GlobalException {
        try {
            LocalDate today = LocalDate.now();
            LocalDate weekAgo = today.minusWeeks(1); // Example range: last week to today
            List<MovieEntity> currentMovies = movieRepository.findByReleaseDateBetween(weekAgo, today);
            return MovieDto.fromEntityList(currentMovies);
        } catch (Exception exception) {
            logger.error("Error getting current movies");
            throw new GlobalException(exception.getMessage(), exception);
        }
    }
}
