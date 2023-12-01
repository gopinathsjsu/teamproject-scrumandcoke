package com.scrumandcoke.movietheaterclub.mapper;

import com.scrumandcoke.movietheaterclub.dto.BookingDto;
import com.scrumandcoke.movietheaterclub.entity.BookingEntity;
import com.scrumandcoke.movietheaterclub.entity.ShowTimeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "showtimeId", source = "bookingEntity.showtime.id")
    BookingDto toDto(BookingEntity bookingEntity);

    @Mapping(target = "bookingId", ignore = true)
    @Mapping(target = "bookingStatus", ignore = true)
    @Mapping(target = "bookingDate", ignore = true)
    @Mapping(target = "showtime", source = "showtimeId", qualifiedByName = "showtimeIdToShowtimeEntity")
    BookingEntity toEntity(BookingDto bookingDto);

    @Mapping(target = "userId", source = "bookingEntity.user.userId")
    @Mapping(target = "showtimeId", source = "bookingEntity.showtime.showtimeId")
    List<BookingDto> toDto(List<BookingEntity> bookingEntities);

    @Named("showtimeIdToShowtimeEntity")
    public static ShowTimeEntity showtimeIdToShowtimeEntity(Integer showTimeId) {
        ShowTimeEntity showTimeEntity = new ShowTimeEntity();
        showTimeEntity.setId(showTimeId);
        return showTimeEntity;
    }
}
