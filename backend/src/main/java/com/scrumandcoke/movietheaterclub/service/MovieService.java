package com.scrumandcoke.movietheaterclub.service;

import com.scrumandcoke.movietheaterclub.dto.MovieDto;
import com.scrumandcoke.movietheaterclub.exception.GlobalException;

import java.util.List;

public interface MovieService {
    void addMovie(MovieDto movieDto) throws GlobalException;
    MovieDto getMovie(Integer movieId) throws GlobalException;
    List<MovieDto> getMovies() throws GlobalException;
    void updateMovie(Integer id, MovieDto movieDto) throws GlobalException;
    void deleteMovie(Integer id) throws GlobalException;

    List<MovieDto> getUpcomingMovies() throws GlobalException;
    List<MovieDto> getCurrentMovies() throws GlobalException;
}