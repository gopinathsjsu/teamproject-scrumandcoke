package com.scrumandcoke.movietheaterclub.service;

import com.scrumandcoke.movietheaterclub.dto.TheaterScreenDto;
import com.scrumandcoke.movietheaterclub.exception.GlobalException;

import java.util.List;

public interface TheaterScreenService {
    void createTheaterScreen(TheaterScreenDto theaterScreenDto)throws GlobalException;
    TheaterScreenDto getTheaterScreenById(int id)throws GlobalException;
    List<TheaterScreenDto> getAllTheaterScreens()throws GlobalException;
    TheaterScreenDto updateTheaterScreen(int id, TheaterScreenDto theaterScreenDto) throws GlobalException;
    void deleteTheaterScreen(int id)throws GlobalException;

    List<TheaterScreenDto> getTheatersByLocationIdV2(int locationId);
}
