package ru.yandex.practicum.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private boolean available;
    private Long requestId; // пока не добавлял в dto т.к. не используется на этом этапе
}
