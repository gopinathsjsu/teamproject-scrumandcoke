package com.scrumandcoke.movietheaterclub.service;

import com.scrumandcoke.movietheaterclub.dto.LocationDto;
import com.scrumandcoke.movietheaterclub.enums.Location;
import com.scrumandcoke.movietheaterclub.exception.GlobalException;

import java.util.List;

public interface MultiplexService {
	void addMultiplex(LocationDto locationDto) throws GlobalException;
	List<LocationDto> getAllMultiplex() throws GlobalException;
	List<LocationDto> getAllMultiplexByLocation(Location location) throws GlobalException;
	void updateMultiplex(Integer id, LocationDto locationDto) throws GlobalException;
	void deleteMultiplex(Integer id) throws GlobalException;
    LocationDto getMultiplex(Integer id) throws GlobalException;
	List<LocationDto> getTheatersByLocationId(int locationId) throws GlobalException;

}

