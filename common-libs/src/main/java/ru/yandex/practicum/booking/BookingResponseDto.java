package ru.yandex.practicum.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.enums.Status;
import ru.yandex.practicum.item.ItemDto;
import ru.yandex.practicum.user.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponseDto {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookingStart;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookingEnd;

    private ItemDto item;

    private UserDto booker;

    private Status status;
}

