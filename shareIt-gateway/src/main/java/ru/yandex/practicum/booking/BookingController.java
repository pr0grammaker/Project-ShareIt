//package ru.yandex.practicum.booking;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import ru.yandex.practicum.enums.BookingState;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/bookings")
//@RequiredArgsConstructor
//public class BookingController {
//    private final BookingService bookingService;
//
//    @PostMapping
//    public ResponseEntity<BookingResponseDto> createBooking(
//            @RequestHeader("X-Sharer-User-Id") long userId,
//            @Valid @RequestBody BookingDto bookingDto) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(userId, bookingDto));
//    }
//
//    @PatchMapping("/{bookingId}")
//    public ResponseEntity<BookingResponseDto> updateApprovalStatus(
//            @PathVariable("bookingId") long bookingId,
//            @RequestHeader("X-Sharer-User-Id") long userId,
//            @RequestParam boolean approved) {
//        return ResponseEntity.ok().body(bookingService.updateApprovalStatus(bookingId, userId, approved));
//    }
//
//    @GetMapping("/{bookingId}")
//    public ResponseEntity<BookingResponseDto> getBookingById(
//            @PathVariable("bookingId") long bookingId,
//            @RequestHeader("X-Sharer-User-Id") long userId
//    ) {
//        return ResponseEntity.ok().body(bookingService.getBookingById(userId, bookingId));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<BookingResponseDto>> getBookingsByBooker(
//            @RequestHeader("X-Sharer-User-Id") long userId,
//            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
//        return ResponseEntity.ok().body(bookingService.getBookingsByBooker(userId, state));
//    }
//
//    @GetMapping("/owner")
//    public ResponseEntity<List<BookingResponseDto>> getBookingsByOwner(
//            @RequestHeader("X-Sharer-User-Id") long userId,
//            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
//        return ResponseEntity.ok().body(bookingService.getBookingsByOwner(userId, state));
//    }
//
//
//}
