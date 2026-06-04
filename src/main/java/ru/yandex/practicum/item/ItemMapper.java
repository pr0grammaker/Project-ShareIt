package ru.yandex.practicum.item;


import org.springframework.stereotype.Service;

@Service
public class ItemMapper {
    public static Item mapToItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
