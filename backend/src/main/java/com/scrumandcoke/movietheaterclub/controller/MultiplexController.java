package com.scrumandcoke.movietheaterclub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PutMapping
    public void updateMultiplex(@RequestBody LocationDto locationDto) throws GlobalException {
        multiplexService.updateMultiplex(locationDto);
    }

    @DeleteMapping("/{id}")
    public void deleteMultiplex(@PathVariable Integer id) throws GlobalException {
        multiplexService.deleteMultiplex(id);
    }
}
