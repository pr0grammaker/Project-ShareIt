package ru.yandex.practicum.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.enums.BookingState;
import ru.yandex.practicum.http.clients.BookingHttpClient;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingHttpClient bookingHttpClient;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        return bookingHttpClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> updateApprovalStatus(
            @PathVariable("bookingId") long bookingId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam boolean approved) {
        return bookingHttpClient.updateApprovalStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingById(
            @PathVariable("bookingId") long bookingId,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        return bookingHttpClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getBookingsByBooker(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return bookingHttpClient.getBookingsByBooker(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return bookingHttpClient.getBookingsByOwner(userId, state);
    }

}
