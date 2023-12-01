package com.scrumandcoke.movietheaterclub.controller;

import com.scrumandcoke.movietheaterclub.annotation.LoginRequired;
import com.scrumandcoke.movietheaterclub.annotation.UserTypesAllowed;
import com.scrumandcoke.movietheaterclub.dto.MovieDto;
import com.scrumandcoke.movietheaterclub.dto.MovieWithShowtimesDto;
import com.scrumandcoke.movietheaterclub.dto.ShowTimeDto;
import com.scrumandcoke.movietheaterclub.dto.UserSessionDetail;
import com.scrumandcoke.movietheaterclub.enums.Location;
import com.scrumandcoke.movietheaterclub.enums.UserType;
import com.scrumandcoke.movietheaterclub.exception.GlobalException;
import com.scrumandcoke.movietheaterclub.service.ShowTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/showtime")
public class ShowTimeController {

    @Autowired
    ShowTimeService showTimeService;

    @PostMapping
    @LoginRequired
    @UserTypesAllowed({UserType.THEATER_EMPLOYEE})
    public ResponseEntity<String> addShowTIme(@RequestBody ShowTimeDto showTimeDto) throws GlobalException {
        try {
            showTimeService.addShowTime(showTimeDto);
            return ResponseEntity.ok("showTime added successfully");
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding showTime: " + e.getMessage());
        }
    }

    @GetMapping("movie/{movieId}")
    public ResponseEntity<?> getshowTimesByMovieId(@PathVariable int movieId) {
        try {
            List<ShowTimeDto> showTimes = showTimeService.getShowTimesByMovie(movieId);
            return ResponseEntity.ok(showTimes);
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting showTimes: " + e.getMessage());
        }
    }

    @GetMapping("screen/{screenId}")
    public ResponseEntity<?> getshowTimesByScreenId(@PathVariable int screenId) {
        try {
            List<ShowTimeDto> showTimes = showTimeService.getShowTimesByTheaterScreenId(screenId);
            return ResponseEntity.ok(showTimes);
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting showTimes: " + e.getMessage());
        }
    }

    @GetMapping("screen/{screenId}/movie/{movieId}")
    public ResponseEntity<?> getshowTimesByScreenIdAndMovieId(@PathVariable int screenId, @PathVariable int movieId) {
        try {
            List<ShowTimeDto> showTimes = showTimeService.getShowTimesByTheaterScreenIdAndMovieId(screenId, movieId);
            return ResponseEntity.ok(showTimes);
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting showTimes: " + e.getMessage());
        }
    }

    @GetMapping("screen/{screenId}/movies")
    public ResponseEntity<?> getMoviesByScreenId(@PathVariable int screenId) {
        try {
            List<MovieDto> movies = showTimeService.getAllMoviesByTheaterScreenId(screenId);
            return ResponseEntity.ok(movies);
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting showTimes: " + e.getMessage());
        }
    }

    @GetMapping("location/{locationName}")
    public ResponseEntity<?> getshowTimesByLocation(@PathVariable Location locationName) {
        try {
            List<ShowTimeDto> showTimes = showTimeService.getShowTimesByLocation(locationName);
            return ResponseEntity.ok(showTimes);
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting showTimes: " + e.getMessage());
        }
    }

    @GetMapping("multiplex/{multiplexId}/screen/{screenId}")
    public ResponseEntity<?> getshowTimesByScreenId(@PathVariable int multiplexId, @PathVariable int screenId) {
        try {
            List<ShowTimeDto> showTimes = showTimeService.getShowTimesByTheaterScreenIdAndMultiplexId(screenId, multiplexId);
            return ResponseEntity.ok(showTimes);
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting showTimes: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWShowTimeById(@PathVariable int id) {
        try {
            ShowTimeDto showTimeDto = showTimeService.getShowTime(id);
            return ResponseEntity.ok(showTimeDto);
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error getting showTime: " + e.getMessage());
        }
    }

    @PutMapping("{id}")
    @LoginRequired
    @UserTypesAllowed({UserType.THEATER_EMPLOYEE})
    public void updateshowTime(@PathVariable int id, @RequestBody ShowTimeDto showTimeDto) throws GlobalException {
        showTimeService.updateShowTime(id, showTimeDto);
    }

    @DeleteMapping("/{id}")
    @LoginRequired
    @UserTypesAllowed({UserType.THEATER_EMPLOYEE})
    public ResponseEntity<?> deleteshowTime(@PathVariable int id) {
        try {
            showTimeService.deleteShowTime(id);
            return ResponseEntity.ok().build();
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error deleting showTime: " + e.getMessage());
        }
    }

    @GetMapping("/movies/upcoming")
    public List<MovieWithShowtimesDto> getMoviesWithUpcomingShowtimes(
            @RequestParam(defaultValue = "7") int daysAhead) {
        return showTimeService.getMoviesWithUpcomingShowtimes(daysAhead);
    }

}
