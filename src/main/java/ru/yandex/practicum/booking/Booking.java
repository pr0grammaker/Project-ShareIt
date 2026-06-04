package ru.yandex.practicum.booking;

import lombok.Data;
import ru.yandex.practicum.enums.Status;
import ru.yandex.practicum.item.Item;

import java.time.LocalDateTime;

@Data
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Long booker;
    private Status status;
}
