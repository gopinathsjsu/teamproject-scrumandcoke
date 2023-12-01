package com.scrumandcoke.movietheaterclub.service.impl;

import com.scrumandcoke.movietheaterclub.dto.LocationDto;
import com.scrumandcoke.movietheaterclub.entity.LocationEntity;
import com.scrumandcoke.movietheaterclub.enums.Location;
import com.scrumandcoke.movietheaterclub.exception.GlobalException;
import com.scrumandcoke.movietheaterclub.repository.MultiplexRepository;
import com.scrumandcoke.movietheaterclub.service.MultiplexService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MultiplexServiceImpl implements MultiplexService {

    @Autowired
    MultiplexRepository multiplexRepository;

    Logger logger = LoggerFactory.getLogger(MultiplexServiceImpl.class);


    @Override
    public void addMultiplex(LocationDto multiplexDto) throws GlobalException {
        if (multiplexDto == null) {
            throw new GlobalException("Multiplex data cannot be null");
        }
        if (multiplexDto.getName() == null || multiplexDto.getName().trim().isEmpty()) {
            throw new GlobalException("Multiplex name cannot be null or empty");
        }
        if (multiplexDto.getLocation() == null) {
            throw new GlobalException("Location cannot be null");
        }
        if (multiplexDto.getTheaterScreenCount() < 1) {
            throw new GlobalException("Theater screen count must be at least 1");
        }
        if (multiplexRepository.existsByName(multiplexDto.getName())) {
            throw new GlobalException("A multiplex with the same name already exists");
        }

        try {
            LocationEntity locationEntity = new LocationEntity();
            locationEntity.setName(multiplexDto.getName());
            locationEntity.setLocation(multiplexDto.getLocation());
            locationEntity.setTheaterScreenCount(multiplexDto.getTheaterScreenCount());
            multiplexRepository.save(locationEntity);
        } catch (Exception exception) {
            logger.error("Error adding multiplex");
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<LocationDto> getAllMultiplex() throws GlobalException {
        try {
            List<LocationEntity> multiplexEntities = multiplexRepository.findAll();
            List<LocationDto> allMultiplex = new ArrayList<>();
            for (LocationEntity locationEntity : multiplexEntities) {
                LocationDto dto = new LocationDto(locationEntity.getId(), locationEntity.getName(), locationEntity.getLocation(), locationEntity.getTheaterScreenCount());
                allMultiplex.add(dto);
            }
            return allMultiplex;
        } catch (Exception exception) {
            logger.error("Error retrieving all multiplexes", exception);
            throw new GlobalException("Error retrieving multiplex data: " + exception.getMessage(), exception);
        }
    }

    @Override
    public List<LocationDto> getAllMultiplexByLocation(Location location) throws GlobalException {
        try {
            List<LocationEntity> multiplexEntities = multiplexRepository.findByLocation(location);
            List<LocationDto> allMultiplex = new ArrayList<>();
            for (LocationEntity locationEntity : multiplexEntities) {
                LocationDto dto = new LocationDto(locationEntity.getId(), locationEntity.getName(),
                        locationEntity.getLocation(), locationEntity.getTheaterScreenCount());
                allMultiplex.add(dto);
            }
            return allMultiplex;
        } catch (Exception exception) {
            logger.error("Error retrieving all multiplexes", exception);
            throw new GlobalException("Error retrieving multiplex data: " + exception.getMessage(), exception);
        }
    }

    @Override
    public void updateMultiplex(Integer id, LocationDto multiplexDto) throws GlobalException {

        if (multiplexDto == null) {
            throw new GlobalException("Multiplex data cannot be null");
        }
        if (id == 0) {
            throw new IllegalArgumentException("Invalid multiplex ID");
        }
        if (multiplexDto.getName() == null || multiplexDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Multiplex name cannot be null or empty");
        }
        if (multiplexDto.getTheaterScreenCount() < 1) {
            throw new IllegalArgumentException("Theater screen count must be at least 1");
        }
        // Check for duplicate name, excluding the current multiplex
        boolean nameExists = multiplexRepository.existsByNameAndIdNot(multiplexDto.getName(), id);
        if (nameExists) {
            throw new IllegalArgumentException("Another multiplex with this name already exists");
        }

        try {
            LocationEntity locationEntity = multiplexRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Multiplex not found with id: " + id));
            locationEntity.setName(multiplexDto.getName());
            locationEntity.setLocation(multiplexDto.getLocation());
            locationEntity.setTheaterScreenCount(multiplexDto.getTheaterScreenCount());
            multiplexRepository.save(locationEntity);
        } catch (Exception e) {
            logger.error("Error updating multiplex with id: " + id, e);
            throw new GlobalException("Error updating multiplex: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteMultiplex(Integer id) throws GlobalException {
        try {
            multiplexRepository.deleteById(id);
        } catch (Exception exception) {
            logger.error("Error deleting multiplex: {}", id);
            throw new GlobalException(exception.getMessage(), exception);
        }
    }

    @Override
    public LocationDto getMultiplex(Integer id) throws GlobalException {
        try {
            LocationEntity locationEntity = multiplexRepository.findById(id).get();
            LocationDto dto = new LocationDto(locationEntity.getId(), locationEntity.getName(), locationEntity.getLocation(), locationEntity.getTheaterScreenCount());
            return dto;
        } catch (Exception exception) {
            logger.error("Error retrieving multiplex {}", id);
            throw new GlobalException("Error retrieving multiplex data: " + exception.getMessage(), exception);
        }
    }

    @Override
    public List<LocationDto> getTheatersByLocationId(int locationId) throws GlobalException {
        try {
            Optional<LocationEntity> locationEntityOptional = multiplexRepository.findById(locationId);
            if (locationEntityOptional.isPresent()) {
                LocationEntity locationEntity = locationEntityOptional.get();
                return Collections.singletonList(convertToDto(locationEntity));
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            throw new GlobalException("Error retrieving data: " + e.getMessage(), e);
        }
    }

    private LocationDto convertToDto(LocationEntity entity) {
        return new LocationDto(entity.getId(), entity.getName(), entity.getLocation(), entity.getTheaterScreenCount());
    }

}
