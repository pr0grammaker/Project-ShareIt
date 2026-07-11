package ru.yandex.practicum.http.clients;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;
import ru.yandex.practicum.booking.BookingDto;
import ru.yandex.practicum.booking.BookingResponseDto;
import ru.yandex.practicum.enums.BookingState;

import java.util.List;

@HttpExchange(
        accept = "application/json",
        contentType = "application/json",
        url = "/bookings"
)
public interface BookingHttpClient {

    @PostExchange
    ResponseEntity<BookingResponseDto> createBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody BookingDto bookingDto);

    @PatchExchange("/{bookingId}")
    ResponseEntity<BookingResponseDto> updateApprovalStatus(
            @PathVariable("bookingId") long bookingId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam boolean approved);

    @GetExchange("/{bookingId}")
    ResponseEntity<BookingResponseDto> getBookingById(
            @PathVariable("bookingId") long bookingId,
            @RequestHeader("X-Sharer-User-Id") long userId);

    @GetExchange
    ResponseEntity<List<BookingResponseDto>> getBookingsByBooker(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state);

    @GetExchange("/owner")
    ResponseEntity<List<BookingResponseDto>> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state);
}
