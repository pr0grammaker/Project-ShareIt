package ru.yandex.practicum.item;

import java.util.Collection;

public interface ItemService {
    Item create(long userId, ItemDto itemDto);

    Item update(long userId, long itemId, ItemDto itemDto);

    Item getItemById(long itemId);

    Collection<Item> getItemsByOwner(long userId);

    Collection<Item> searchByText(String text);
}
