package ru.yandex.practicum.booking;

import lombok.Data;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingDto {

    private Long id;

//    @NotNull(message = "Дата начала бронирования обязательна")
//    @Future(message = "Дата начала должна быть в будущем")
    private LocalDateTime bookingStart;

//    @NotNull(message = "Дата окончания бронирования обязательна")
//    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime bookingEnd;

    private Long itemId;
}

