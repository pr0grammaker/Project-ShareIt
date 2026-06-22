package ru.yandex.practicum.booking;

import ru.yandex.practicum.enums.BookingState;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(long userId, BookingDto bookingDto);

    BookingResponseDto updateApprovalStatus(long bookingId, long ownerId, boolean approved);

    BookingResponseDto getBookingById(long bookingId, long userId);

    List<BookingResponseDto> getBookingsByBooker(long userId, BookingState state);

    List<BookingResponseDto> getBookingsByOwner(long userId, BookingState state);
}
