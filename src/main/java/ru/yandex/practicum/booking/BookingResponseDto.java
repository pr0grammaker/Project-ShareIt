package ru.yandex.practicum.booking;

import lombok.Data;
import ru.yandex.practicum.enums.Status;
import ru.yandex.practicum.item.ItemDto;
import ru.yandex.practicum.user.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingResponseDto {

    private Long id;

    private LocalDateTime bookingStart;

    private LocalDateTime bookingEnd;

    private ItemDto item;

    private UserDto booker;

    private Status status;
}

