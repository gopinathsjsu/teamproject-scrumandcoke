package com.scrumandcoke.movietheaterclub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieWithShowtimesDto {
    private Integer movieId;
    private String movieName;
    private String synopsis;

    private String posterUrl;


    private List<ShowTimeDto> upcomingShowtimes;

}
