package com.scrumandcoke.movietheaterclub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TheaterScreenDto {
	  private int id;
	    private String name;
	    private MultiplexDto multiplex;
	    private int seatingCapacity;
}
