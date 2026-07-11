package ru.yandex.practicum.booking;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {

    private Long id;
    private LocalDateTime bookingStart;
    private LocalDateTime bookingEnd;
    private Long itemId;
}

