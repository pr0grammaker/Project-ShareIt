package ru.yandex.practicum.booking;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {
    BookingResponseDto mapToBookingResponseDto(Booking booking);

    List<BookingResponseDto> mapToBookingListDto(List<Booking> bookings);

}
