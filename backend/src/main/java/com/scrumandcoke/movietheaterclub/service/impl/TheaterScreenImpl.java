package com.scrumandcoke.movietheaterclub.service.impl;

import com.scrumandcoke.movietheaterclub.dto.TheaterScreenDto;
import com.scrumandcoke.movietheaterclub.entity.LocationEntity;
import com.scrumandcoke.movietheaterclub.entity.TheaterScreenEntity;
import com.scrumandcoke.movietheaterclub.exception.GlobalException;
import com.scrumandcoke.movietheaterclub.repository.MultiplexRepository;
import com.scrumandcoke.movietheaterclub.repository.TheaterScreenRepository;
import com.scrumandcoke.movietheaterclub.service.TheaterScreenService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TheaterScreenImpl implements TheaterScreenService {

    @Autowired
    TheaterScreenRepository theaterScreenRepository;

    @Autowired
    MultiplexRepository multiplexRepository;

    Logger logger = LoggerFactory.getLogger(MultiplexServiceImpl.class);

    @Override
    @Transactional
    public void createTheaterScreen(TheaterScreenDto theaterScreenDto) throws GlobalException {
        try {
            TheaterScreenEntity theaterScreenEntity = new TheaterScreenEntity();
            theaterScreenEntity.setName(theaterScreenDto.getName());
            theaterScreenEntity.setSeatingCapacity(theaterScreenDto.getSeatingCapacity());
            theaterScreenEntity.setAddress(theaterScreenDto.getAddress());
            theaterScreenEntity.setPhone(theaterScreenDto.getPhone());
            theaterScreenEntity.setEmail(theaterScreenDto.getEmail());

            LocationEntity locationEntity = multiplexRepository.findById(theaterScreenDto.getMultiplexId())
                    .orElseThrow(() -> new GlobalException("Multiplex not found"));
            theaterScreenEntity.setLocationEntity(locationEntity);

            theaterScreenRepository.save(theaterScreenEntity);
        } catch (Exception exception) {
            logger.error("Error adding theater screen", exception);
            throw new GlobalException("Error adding theater screen: " + exception.getMessage(), exception);
        }
    }

    @Override
    public TheaterScreenDto getTheaterScreenById(int id) throws GlobalException {
        try {
            Optional<TheaterScreenEntity> theaterScreenOptional = theaterScreenRepository.findById(id);

            TheaterScreenEntity theaterScreenEntity = theaterScreenOptional.get();
            TheaterScreenDto theaterScreenDto = new TheaterScreenDto();
            theaterScreenDto.setId(theaterScreenEntity.getId());
            theaterScreenDto.setName(theaterScreenEntity.getName());

            theaterScreenDto.setAddress(theaterScreenEntity.getAddress());
            theaterScreenDto.setPhone(theaterScreenEntity.getPhone());
            theaterScreenDto.setEmail(theaterScreenEntity.getEmail());

            theaterScreenDto.setSeatingCapacity(theaterScreenEntity.getSeatingCapacity());
            theaterScreenDto.setMultiplexId(theaterScreenEntity.getLocationEntity().getId());

            return theaterScreenDto;
        } catch (Exception e) {
            logger.error("Error retrieving theater screen with ID: " + id, e);
            throw new GlobalException("Error retrieving theater screen: " + e.getMessage(), e);
        }
    }

    @Override
    public TheaterScreenDto updateTheaterScreen(int id, TheaterScreenDto theaterScreenDto) throws GlobalException {
        try {
            Optional<TheaterScreenEntity> theaterScreenOptional = theaterScreenRepository.findById(id);
            if (!theaterScreenOptional.isPresent()) {
                throw new GlobalException("Theater screen not found for ID: " + id);
            }

            TheaterScreenEntity theaterScreenEntity = theaterScreenOptional.get();

            //new validation code
            configureSeatingCapacity(id, theaterScreenDto.getSeatingCapacity());

            theaterScreenEntity.setName(theaterScreenDto.getName());
            theaterScreenEntity.setSeatingCapacity(theaterScreenDto.getSeatingCapacity());
            theaterScreenEntity.setEmail(theaterScreenDto.getEmail());
            theaterScreenEntity.setAddress(theaterScreenDto.getAddress());
            theaterScreenEntity.setPhone(theaterScreenDto.getPhone());

            if (theaterScreenDto.getMultiplexId() != null && theaterScreenDto.getMultiplexId() != 0) {
                LocationEntity locationEntity = multiplexRepository.findById(theaterScreenDto.getMultiplexId())
                        .orElseThrow(() -> new GlobalException("Multiplex not found"));
                theaterScreenEntity.setLocationEntity(locationEntity);
            }

            TheaterScreenEntity updatedTheaterScreenEntity = theaterScreenRepository.save(theaterScreenEntity);

            TheaterScreenDto updatedDto = new TheaterScreenDto();
            updatedDto.setId(updatedTheaterScreenEntity.getId());
            updatedDto.setName(updatedTheaterScreenEntity.getName());

            updatedDto.setEmail(updatedTheaterScreenEntity.getEmail());
            updatedDto.setAddress(updatedTheaterScreenEntity.getAddress());
            updatedDto.setPhone(updatedTheaterScreenEntity.getPhone());

            updatedDto.setSeatingCapacity(updatedTheaterScreenEntity.getSeatingCapacity());
            updatedDto.setMultiplexId(updatedTheaterScreenEntity.getLocationEntity().getId());

            return updatedDto;
        } catch (Exception e) {
            logger.error("Error updating theater screen with ID: " + id, e);
            throw new GlobalException("Error updating theater screen: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteTheaterScreen(int id) throws GlobalException {
        try {
            Optional<TheaterScreenEntity> theaterScreenOptional = theaterScreenRepository.findById(id);

            if (!theaterScreenOptional.isPresent()) {
                throw new GlobalException("Theater screen not found for ID: " + id);
            }

            theaterScreenRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting theater screen with ID: " + id, e);
            throw new GlobalException("Error deleting theater screen: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TheaterScreenDto> getTheatersByLocationIdV2(int locationId) {
        List<TheaterScreenEntity> theaterScreenEntities = theaterScreenRepository.findByLocationEntity_Id(locationId);
        if (CollectionUtils.isEmpty(theaterScreenEntities)) {
            return Collections.emptyList();
        }
        return getTheaterScreenDtos(theaterScreenEntities);
    }

    @Override
    public List<TheaterScreenDto> getAllTheaterScreens() throws GlobalException {
        try {
            List<TheaterScreenEntity> theaterScreenEntities = theaterScreenRepository.findAll();
            List<TheaterScreenDto> theaterScreenDtos = getTheaterScreenDtos(theaterScreenEntities);

            return theaterScreenDtos;
        } catch (Exception e) {
            logger.error("Error retrieving all theater screens", e);
            throw new GlobalException("Error retrieving theater screens", e);
        }
    }

    private static List<TheaterScreenDto> getTheaterScreenDtos(List<TheaterScreenEntity> theaterScreenEntities) {
        List<TheaterScreenDto> theaterScreenDtos = new ArrayList<>();

        for (TheaterScreenEntity theaterScreenEntity : theaterScreenEntities) {
            TheaterScreenDto dto = new TheaterScreenDto();
            dto.setId(theaterScreenEntity.getId());
            dto.setName(theaterScreenEntity.getName());
            dto.setSeatingCapacity(theaterScreenEntity.getSeatingCapacity());
            dto.setMultiplexId(theaterScreenEntity.getLocationEntity().getId());

            dto.setPhone(theaterScreenEntity.getPhone());
            dto.setAddress(theaterScreenEntity.getAddress());
            dto.setEmail(theaterScreenEntity.getEmail());

            theaterScreenDtos.add(dto);
        }
        return theaterScreenDtos;
    }

    //validations
    public void configureSeatingCapacity(int theaterScreenId, int newSeatingCapacity) throws GlobalException {
        validateSeatingCapacity(theaterScreenId, newSeatingCapacity);

        TheaterScreenEntity theaterScreenEntity = theaterScreenRepository.findById(theaterScreenId)
                .orElseThrow(() -> new GlobalException("Theater screen not found for ID: " + theaterScreenId));
        theaterScreenEntity.setSeatingCapacity(newSeatingCapacity);
        theaterScreenRepository.save(theaterScreenEntity);
    }

    private void validateSeatingCapacity(int theaterScreenId, int newSeatingCapacity) throws GlobalException {
        if (newSeatingCapacity <= 0) {
            throw new GlobalException("Invalid Seating Capacity: Seating capacity cannot be zero or negative.");
        }

        int physicalCapacity = getPhysicalTheaterCapacity(theaterScreenId);
        if (newSeatingCapacity > physicalCapacity) {
            throw new GlobalException("Exceeding Physical Capacity: Configured capacity exceeds the theater's physical limits.");
        }
    }

    private int getPhysicalTheaterCapacity(int theaterScreenId) throws GlobalException {
        TheaterScreenEntity theater = theaterScreenRepository.findById(theaterScreenId)
                .orElseThrow(() -> new GlobalException("Theater with the specified ID does not exist"));
        return theater.getSeatingCapacity();
    }


}
