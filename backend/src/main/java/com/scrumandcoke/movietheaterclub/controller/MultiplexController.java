package com.scrumandcoke.movietheaterclub.controller;

import java.util.List;

import com.scrumandcoke.movietheaterclub.dto.ShowTimeDto;
import com.scrumandcoke.movietheaterclub.enums.Location;
import com.scrumandcoke.movietheaterclub.service.ShowTimeService;
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

import com.scrumandcoke.movietheaterclub.dto.MultiplexDto;
import com.scrumandcoke.movietheaterclub.exception.GlobalException;
import com.scrumandcoke.movietheaterclub.service.MultiplexService;

@RestController
@RequestMapping("/multiplex")
public class MultiplexController {

    @Autowired
    MultiplexService multiplexService;

    @Autowired
    private ShowTimeService showTimeService;

    @GetMapping
    public List<MultiplexDto> getAllMultiplex() throws GlobalException {
        return multiplexService.getAllMultiplex();
    }

    @GetMapping("/{id}")
    public MultiplexDto getMultiplex(@PathVariable Integer id) throws GlobalException {
        return multiplexService.getMultiplex(id);
    }

    @GetMapping("/location/{locationName}")
    public List<MultiplexDto> getMultiplexByLocation(@PathVariable Location locationName) throws GlobalException {
        return multiplexService.getAllMultiplexByLocation(locationName);
    }

    @PostMapping
    public void addMultiplex(@RequestBody MultiplexDto multiplexDto) throws GlobalException {
        multiplexService.addMultiplex(multiplexDto);
    }

    @PutMapping
    public void updateMultiplex(@RequestBody MultiplexDto multiplexDto) throws GlobalException {
        multiplexService.updateMultiplex(multiplexDto);
    }

    @DeleteMapping("/{id}")
    public void deleteMultiplex(@PathVariable Integer id) throws GlobalException {
        multiplexService.deleteMultiplex(id);
    }
}
