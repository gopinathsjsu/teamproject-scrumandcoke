package com.scrumandcoke.movietheaterclub.service.impl;

import com.scrumandcoke.movietheaterclub.dto.MovieDto;
import com.scrumandcoke.movietheaterclub.dto.MovieWithShowtimesDto;
import com.scrumandcoke.movietheaterclub.dto.ShowTimeDto;
import com.scrumandcoke.movietheaterclub.entity.MovieEntity;
import com.scrumandcoke.movietheaterclub.entity.ShowTimeEntity;
import com.scrumandcoke.movietheaterclub.enums.Location;
import com.scrumandcoke.movietheaterclub.exception.GlobalException;
import com.scrumandcoke.movietheaterclub.mapper.ShowTimeMapper;
import com.scrumandcoke.movietheaterclub.repository.MovieRepository;
import com.scrumandcoke.movietheaterclub.repository.ShowTimeRepository;
import com.scrumandcoke.movietheaterclub.service.ShowTimeService;
import com.scrumandcoke.movietheaterclub.service.TheaterScreenService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShowTimeServiceImpl implements ShowTimeService {

    @Autowired
    ShowTimeRepository showTimeRepository;

    @Autowired
    TheaterScreenService theaterScreenService;

    @Autowired
    MovieRepository movieRepository;

    @Value("${showtime.discount.percent:10}")
    private double discount;

    @Autowired
    ShowTimeMapper showTimeMapper;

    Logger logger = LoggerFactory.getLogger(ShowTimeService.class);


    @Override
    public void addShowTime(ShowTimeDto showTimeDto) throws GlobalException {
        if (showTimeDto == null) {
            throw new GlobalException("Showtime data cannot be null");
        }
        if (showTimeDto.getTime() != null && showTimeDto.getTime().before(new Date())) {
            throw new GlobalException("Showtime must be set for a future date and time");
        }
        try {
            validateDiscountedPrice(showTimeDto);
            ShowTimeEntity showTimeEntity = showTimeMapper.toEntity(showTimeDto);
            showTimeEntity.setAvailableSeats(theaterScreenService.getTheaterScreenById(
                    showTimeDto.getTheaterScreenId()).getSeatingCapacity());
            showTimeRepository.save(showTimeEntity);
        } catch (Exception exception) {
            logger.error("Error saving showtime: {}", showTimeDto.getId());
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public ShowTimeDto getShowTime(Integer id) throws GlobalException {
        if (id == null) {
            throw new GlobalException("Showtime ID cannot be null");
        }
        try {
            return showTimeMapper.toDto(showTimeRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Showtime not found with ID: " + id)));
        } catch (Exception exception) {
            logger.error("Error getting showtime: {}", id);
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<ShowTimeDto> getShowTimesByMovie(Integer id) throws GlobalException {
        try {
            return showTimeMapper.toDto(showTimeRepository.findByMovie_MovieId(id));
        } catch (Exception exception) {
            logger.error("Error getting showtime with movieId: {}", id);
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<MovieDto> getAllMoviesByTheaterScreenId(Integer id) throws GlobalException {
        try {
            List<ShowTimeEntity> showTimeEntities = showTimeRepository.findByTheaterScreen_Id(id);
            Set<MovieEntity> uniqueMovies = showTimeEntities.stream()
                    .map(ShowTimeEntity::getMovie)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            return MovieDto.fromEntityList(uniqueMovies);
        } catch (Exception exception) {
            logger.error("Error getting showtime with movieId: {}", id);
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<ShowTimeDto> getShowTimesByTheaterScreenId(Integer id) throws GlobalException {
        try {
            return showTimeMapper.toDto(showTimeRepository.findByTheaterScreen_Id(id));
        } catch (Exception exception) {
            logger.error("Error getting showtime with theaterScreenId: {}", id);
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<ShowTimeDto> getShowTimesByTheaterScreenIdAndMovieId(Integer theaterId, Integer movieId) throws GlobalException {
        try {
            return showTimeMapper.toDto(showTimeRepository.findByTheaterScreen_IdAndMovie_MovieId(theaterId, movieId));
        } catch (Exception exception) {
            logger.error("Error getting showtime with theaterScreenId and movieId: {} {}", theaterId, movieId);
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<ShowTimeDto> getShowTimesByLocation(Location location) throws GlobalException {
        try {
            return showTimeMapper.toDto(showTimeRepository.findByTheaterScreen_LocationEntity_Location(location));
        } catch (Exception exception) {
            logger.error("Error getting showtime with location: {}", location);
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<ShowTimeDto> getShowTimesByTheaterScreenIdAndMultiplexId(Integer theaterScreenId, Integer multiplexId) throws GlobalException {
        try {
            return showTimeMapper.toDto(showTimeRepository
                    .findByTheaterScreen_IdAndTheaterScreen_LocationEntity_Id(theaterScreenId, multiplexId));
        } catch (Exception exception) {
            logger.error("Error getting showtime with theaterScreenId: {} and multiplexId: {}", theaterScreenId,
                    multiplexId);
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public void updateShowTime(Integer id, ShowTimeDto showTimeDto) throws GlobalException {
        if (showTimeDto == null) {
            throw new GlobalException("Showtime data cannot be null");
        }
        if (showTimeDto.getTime() != null && showTimeDto.getTime().before(new Date())) {
            throw new GlobalException("Showtime must be set for a future date and time");
        }
        try {
            validateDiscountedPrice(showTimeDto);
            ShowTimeEntity entity = showTimeRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Showtime not found with id: " + showTimeDto.getId()));
            showTimeMapper.updateCustomerFromDto(showTimeDto, entity);
            showTimeRepository.save(entity);
        } catch (Exception exception) {
            logger.error("Error updating showtime: {}", showTimeDto.getId());
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deleteShowTime(Integer id) throws GlobalException {
        if (id == null) {
            throw new GlobalException("Showtime ID cannot be null");
        }
        try {
            if (!showTimeRepository.existsById(id)) {
                throw new EntityNotFoundException("Showtime not found with ID: " + id);
            }
            showTimeRepository.deleteById(id);
        } catch (Exception exception) {
            logger.error("Error deleting showtime: {}", id);
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    public void validateDiscountedPrice(ShowTimeDto showTimeDto) {
        if (showTimeDto.getTime() == null || showTimeDto.getPrice() == null) {
            throw new IllegalArgumentException("Time and price must be set for validation.");
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(showTimeDto.getTime());

        boolean isTuesday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY;
        boolean isBefore6PM = calendar.get(Calendar.HOUR_OF_DAY) < 18;

        if (isTuesday && isBefore6PM) {
            if (showTimeDto.getDiscountedPrice() != null && showTimeDto.getDiscountedPrice() < showTimeDto.getPrice()) {
            } else {
                throw new IllegalArgumentException("Discounted price is not valid for the given time or is higher than the regular price.");
            }
        } else if (showTimeDto.getDiscountedPrice() != null) {
            throw new IllegalArgumentException("Discounted price can only be set for shows on Tuesday before 6 PM.");
        }
    }

    @Override
    public Double getDiscountedPrice(Double price, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (dayOfWeek == Calendar.TUESDAY || (hourOfDay >= 8 && hourOfDay < 18)) {
            double discountFactor = 1.0 - (discount / 100.0);
            price = price * discountFactor;
        }
        return price;
    }

    @Override
    public List<MovieWithShowtimesDto> getMoviesWithUpcomingShowtimes(int daysAhead) {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DATE, daysAhead);
        Date endDate = c.getTime();

        List<ShowTimeEntity> showTimeEntities = showTimeRepository.findByTimeBetween(today, endDate);
        List<ShowTimeDto> showTimeDtos = showTimeMapper.toDto(showTimeEntities);

        // Grouping showtimes by movieId
        Map<Integer, List<ShowTimeDto>> groupedShowTimes = showTimeDtos.stream()
                .collect(Collectors.groupingBy(ShowTimeDto::getMovieId));

        // Creating a list of MovieWithShowtimesDto
        List<MovieWithShowtimesDto> moviesWithShowtimes = new ArrayList<>();
        for (Map.Entry<Integer, List<ShowTimeDto>> entry : groupedShowTimes.entrySet()) {
            Integer movieId = entry.getKey();
            List<ShowTimeDto> showTimes = entry.getValue();

            MovieWithShowtimesDto movieWithShowtimesDto = new MovieWithShowtimesDto();
            movieWithShowtimesDto.setMovieId(movieId);
//            movieWithShowtimesDto.setUpcomingShowtimes(showTimes);

            // Assuming you have a method to fetch movie details by movieId
            MovieEntity movie = movieRepository.findById(movieId).orElse(null);
            if (movie == null || Objects.isNull(movie.getReleaseDate())) {
                continue;
            }

            Date releaseDate = movie.getReleaseDate();  // This is your Date object
            LocalDate localReleaseDate = releaseDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            movieWithShowtimesDto.setMovieName(movie.getMovieName());
            movieWithShowtimesDto.setSynopsis(movie.getSynopsis());
            movieWithShowtimesDto.setPosterUrl(movie.getPosterUrl());
                // Set other movie details as needed

            moviesWithShowtimes.add(movieWithShowtimesDto);
        }

        return moviesWithShowtimes;
    }

}
