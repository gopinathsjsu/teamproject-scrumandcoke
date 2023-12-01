package com.scrumandcoke.movietheaterclub.controller;

import com.scrumandcoke.movietheaterclub.annotation.LoginRequired;
import com.scrumandcoke.movietheaterclub.annotation.UserTypesAllowed;
import com.scrumandcoke.movietheaterclub.dto.MovieDto;
import com.scrumandcoke.movietheaterclub.enums.UserType;
import com.scrumandcoke.movietheaterclub.exception.GlobalException;
import com.scrumandcoke.movietheaterclub.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    MovieService movieService;



    @PostMapping
    @LoginRequired
    @UserTypesAllowed({UserType.THEATER_EMPLOYEE})
    public ResponseEntity<String> addMovie(@RequestBody MovieDto movieDto) throws GlobalException {
        try {
            movieService.addMovie(movieDto);
            return ResponseEntity.ok("Movie added successfully");
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding movie: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllMovies() {
        try {
            List<MovieDto> movies = movieService.getMovies();
            return ResponseEntity.ok(movies);
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting movies: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable int id) {
        try {
            MovieDto movieDto = movieService.getMovie(id);
            return ResponseEntity.ok(movieDto);
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error getting movie: " + e.getMessage());
        }
    }

    @PutMapping("{id}")
    @LoginRequired
    @UserTypesAllowed({UserType.THEATER_EMPLOYEE})
    public void updateMovie(@PathVariable int id, @RequestBody MovieDto movieDto) throws GlobalException {
            movieService.updateMovie(id, movieDto);
    }

    @DeleteMapping("/{id}")
    @LoginRequired
    @UserTypesAllowed({UserType.THEATER_EMPLOYEE})
    public ResponseEntity<?> deleteMovie(@PathVariable int id) {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.ok().build();
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error deleting movie: " + e.getMessage());
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingMovies() {
        try {
            return ResponseEntity.ok(movieService.getUpcomingMovies());
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error displaying upcoming movie list");
        }
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentMovies() {
        try {
            return ResponseEntity.ok(movieService.getCurrentMovies());
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error displaying current movie list");
        }
    }

}
