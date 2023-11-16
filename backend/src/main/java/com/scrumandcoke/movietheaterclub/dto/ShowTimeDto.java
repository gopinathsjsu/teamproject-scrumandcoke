package com.scrumandcoke.movietheaterclub.dto;

import com.scrumandcoke.movietheaterclub.model.MovieEntity;
import com.scrumandcoke.movietheaterclub.model.ShowTimeEntity;
import com.scrumandcoke.movietheaterclub.model.TheaterScreenEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowTimeDto {
    private Integer id;
    private Date time;
    private Integer movieId;
    private Integer theaterScreenId;
    private Double price;

    public static ShowTimeDto fromEntity(ShowTimeEntity showTimeEntity) {
        ShowTimeDto showTimeDto = new ShowTimeDto();
        showTimeDto.setId(showTimeEntity.getId());
        showTimeDto.setTime(showTimeEntity.getTime());
        showTimeDto.setPrice(showTimeEntity.getPrice());
        showTimeDto.setMovieId(showTimeEntity.getMovie().getMovieId());
        showTimeDto.setTheaterScreenId(showTimeEntity.getTheaterScreen().getId());
        return showTimeDto;
    }

    public static List<ShowTimeDto> fromEntityList(List<ShowTimeEntity> showTimeEntities) {
        List<ShowTimeDto> showTimeDtoList = new ArrayList<>();
        for (ShowTimeEntity showTime : showTimeEntities) {
            showTimeDtoList.add(fromEntity(showTime));
        }
        return showTimeDtoList;
    }

    public static ShowTimeEntity toEntity(ShowTimeDto showTimeDto) {
        ShowTimeEntity showTimeEntity = new ShowTimeEntity();
        showTimeEntity.setId(showTimeDto.getId());
        showTimeEntity.setTime(showTimeDto.getTime());
        showTimeEntity.setPrice(showTimeDto.getPrice());
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setMovieId(showTimeDto.getMovieId());
        showTimeEntity.setMovie(movieEntity);
        TheaterScreenEntity theaterScreen = new TheaterScreenEntity();
        theaterScreen.setId(showTimeDto.getTheaterScreenId());
        showTimeEntity.setTheaterScreen(theaterScreen);
        return showTimeEntity;
    }
}