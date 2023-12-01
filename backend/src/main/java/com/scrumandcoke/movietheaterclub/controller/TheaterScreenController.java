package com.scrumandcoke.movietheaterclub.controller;

import java.util.List;

import com.scrumandcoke.movietheaterclub.annotation.LoginRequired;
import com.scrumandcoke.movietheaterclub.annotation.UserTypesAllowed;
import com.scrumandcoke.movietheaterclub.enums.UserType;
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

import com.scrumandcoke.movietheaterclub.dto.TheaterScreenDto;
import com.scrumandcoke.movietheaterclub.exception.GlobalException;
import com.scrumandcoke.movietheaterclub.service.TheaterScreenService;

@RestController
@RequestMapping("/screen")
public class TheaterScreenController {

	@Autowired
	TheaterScreenService theaterScreenService;

	@PostMapping
    @LoginRequired
    @UserTypesAllowed({UserType.THEATER_EMPLOYEE})
	public ResponseEntity<String> createTheaterScreen(@RequestBody TheaterScreenDto theaterScreenDto) throws GlobalException {
        try {
            theaterScreenService.createTheaterScreen(theaterScreenDto);
            return ResponseEntity.ok("Theater screen created successfully");
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error creating theater screen: " + e.getMessage());
        }
    }
	
	@GetMapping
    public ResponseEntity<List<TheaterScreenDto>> getAllTheaterScreens() {
        try {
            List<TheaterScreenDto> theaterScreens = theaterScreenService.getAllTheaterScreens();
            return ResponseEntity.ok(theaterScreens);
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);
        }
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<TheaterScreenDto> getTheaterScreenById(@PathVariable int id) {
        try {
            TheaterScreenDto theaterScreenDto = theaterScreenService.getTheaterScreenById(id);
            return ResponseEntity.ok(theaterScreenDto);
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
	
	@PutMapping("/{id}")
    @LoginRequired
    @UserTypesAllowed({UserType.THEATER_EMPLOYEE})
    public ResponseEntity<TheaterScreenDto> updateTheaterScreen(@PathVariable int id, @RequestBody TheaterScreenDto theaterScreenDto) {
        try {
            TheaterScreenDto updatedDto = theaterScreenService.updateTheaterScreen(id, theaterScreenDto);
            return ResponseEntity.ok(updatedDto);
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
	
	@DeleteMapping("/{id}")
    @LoginRequired
    @UserTypesAllowed({UserType.THEATER_EMPLOYEE})
    public ResponseEntity<?> deleteTheaterScreen(@PathVariable int id) {
        try {
            theaterScreenService.deleteTheaterScreen(id);
            return ResponseEntity.ok().build();
        } catch (GlobalException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }
	
	
}
