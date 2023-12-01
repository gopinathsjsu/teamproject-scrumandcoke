package com.scrumandcoke.movietheaterclub.controller;

import java.util.List;

import com.scrumandcoke.movietheaterclub.dto.TheaterScreenDto;
import com.scrumandcoke.movietheaterclub.service.TheaterScreenService;
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

import com.scrumandcoke.movietheaterclub.dto.LocationDto;
import com.scrumandcoke.movietheaterclub.exception.GlobalException;
import com.scrumandcoke.movietheaterclub.service.MultiplexService;

@RestController
@RequestMapping("/multiplex")
public class MultiplexController {

    @Autowired
    MultiplexService multiplexService;


    @Autowired
    TheaterScreenService theaterScreenService;

    @GetMapping
    public List<LocationDto> getAllMultiplex() throws GlobalException {
        return multiplexService.getAllMultiplex();
    }

    @GetMapping("/{id}")
    public LocationDto getMultiplex(@PathVariable Integer id) throws GlobalException {
        return multiplexService.getMultiplex(id);
    }

//    @GetMapping("/location/{locationName}")
//    public List<MultiplexDto> getMultiplexByLocation(@PathVariable Location locationName) throws GlobalException {
//        return multiplexService.getAllMultiplexByLocation(locationName);
//    }

    @PostMapping
    public void addMultiplex(@RequestBody LocationDto locationDto) throws GlobalException {
        multiplexService.addMultiplex(locationDto);
    }

    @PutMapping("{id}")
    public void updateMultiplex(@PathVariable int id, @RequestBody LocationDto locationDto) throws GlobalException {
        multiplexService.updateMultiplex(id, locationDto);
    }

    @DeleteMapping("/{id}")
    public void deleteMultiplex(@PathVariable Integer id) throws GlobalException {
        multiplexService.deleteMultiplex(id);
    }
    @GetMapping("theater/{locationId}")
    @Deprecated
    public ResponseEntity<List<LocationDto>> getTheatersByLocationId(@PathVariable int locationId) {
        try {
            List<LocationDto> theaters = multiplexService.getTheatersByLocationId(locationId);
            return ResponseEntity.ok(theaters);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("theater/v2/{locationId}")
    public List<TheaterScreenDto> getTheatersByLocationIdv2(@PathVariable int locationId) {
        return theaterScreenService.getTheatersByLocationIdV2(locationId);
    }
}
