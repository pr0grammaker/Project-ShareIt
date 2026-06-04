package ru.yandex.practicum.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequest {

    private Long id;

    private Long requestorId;

    private String description;

    private LocalDateTime created;
}