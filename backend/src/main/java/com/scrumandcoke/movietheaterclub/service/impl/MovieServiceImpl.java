package com.scrumandcoke.movietheaterclub.service.impl;

import com.scrumandcoke.movietheaterclub.dto.MovieDto;
import com.scrumandcoke.movietheaterclub.entity.MovieEntity;
import com.scrumandcoke.movietheaterclub.exception.GlobalException;
import com.scrumandcoke.movietheaterclub.repository.MovieRepository;
import com.scrumandcoke.movietheaterclub.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    MovieRepository movieRepository;

    Logger logger = LoggerFactory.getLogger(MovieServiceImpl.class);

    @Override
    public void addMovie(MovieDto movieDto) throws GlobalException {
        try {
            validateMovieDto(movieDto); //new validation call
            ensureUniqueMovieTitle(movieDto.getMovieName()); //new validation call
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
            validateMovieDto(movieDto); //new call
            MovieEntity movie = getValidatedMovieEntity(movieDto.getMovieId()); //new call
            MovieEntity movieEntity = movieRepository.findById(movieDto.getMovieId()).get();
            movieEntity.setMovieName(movieDto.getMovieName());
            movieEntity.setSynopsis(movieDto.getSynopsis());
            movieEntity.setRunningTime(movieDto.getRunningTime());

            movieEntity.setGenre(movieDto.getGenre());
            movieEntity.setLanguage(movieDto.getLanguage());
            movieEntity.setReleaseDate(movieDto.getReleaseDate());

            movieEntity.setPosterUrl(movieDto.getPosterUrl());

            movieRepository.save(movieEntity);

        } catch (Exception exception) {
            logger.error("Error updating movie: {}", movieDto.getMovieName());
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deleteMovie(Integer id) throws GlobalException {
        try {
            getValidatedMovieEntity(id); // Check if movie exists + new call
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

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            logger.info("Current date (set to midnight): {}", sdf.format(today));

            List<MovieEntity> upcomingMovies = movieRepository.findByReleaseDateAfter(today);
            return MovieDto.fromEntityList(upcomingMovies);
        } catch (Exception exception) {
            logger.error("Error getting upcoming movies", exception);
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<MovieDto> getCurrentMovies() throws GlobalException {
        try {
            // Get today's date with time set to the start of the day
            Calendar calendarToday = Calendar.getInstance();
            calendarToday.set(Calendar.HOUR_OF_DAY, 0);
            calendarToday.clear(Calendar.MINUTE);
            calendarToday.clear(Calendar.SECOND);
            calendarToday.clear(Calendar.MILLISECOND);

            Date today = calendarToday.getTime();

            // Get date one week ago
            Calendar calendarWeekAgo = Calendar.getInstance();
            calendarWeekAgo.add(Calendar.WEEK_OF_YEAR, -1); // Subtract one week
            calendarWeekAgo.set(Calendar.HOUR_OF_DAY, 0);
            calendarWeekAgo.clear(Calendar.MINUTE);
            calendarWeekAgo.clear(Calendar.SECOND);
            calendarWeekAgo.clear(Calendar.MILLISECOND);

            Date weekAgo = calendarWeekAgo.getTime();

            // Fetch movies released between a week ago and today
            List<MovieEntity> currentMovies = movieRepository.findByReleaseDateBetween(weekAgo, today);
            return MovieDto.fromEntityList(currentMovies);
        } catch (Exception exception) {
            logger.error("Error getting current movies", exception);
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<MovieDto> getCurrentMoviesByTheater(Integer multiplexId) throws GlobalException {
        try {
            Date today = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            calendar.add(Calendar.DAY_OF_MONTH, -7); // Set to one week ago
            Date oneWeekAgo = calendar.getTime();

            List<MovieEntity> recentMovies = movieRepository.findMoviesWithRecentRelease(multiplexId, oneWeekAgo, today);

            return recentMovies.stream()
                    .map(MovieDto::fromEntity) // Assuming you have a method to convert entity to DTO
                    .collect(Collectors.toList());
        } catch (Exception exception) {
            throw new GlobalException("Error retrieving movies: " + exception.getMessage());
        }
    }

    private void validateMovieDto(MovieDto movieDto) throws GlobalException {
        if (movieDto == null) {
            throw new GlobalException("Movie details cannot be null");
        }
    }

    private void ensureUniqueMovieTitle(String movieTitle) throws GlobalException {
        if (movieRepository.findByMovieName(movieTitle).isPresent()) {
            throw new GlobalException("Movie title already exists");
        }
    }

    private MovieEntity getValidatedMovieEntity(Integer movieId) throws GlobalException {
        return movieRepository.findById(movieId).orElseThrow(() ->
                new GlobalException("Movie with the specified ID does not exist"));
    }



}

