package ru.yandex.practicum.item;

import java.util.Collection;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemById(long itemId);

    Collection<ItemDto> getItemsByOwner(long userId);

    Collection<ItemDto> searchByText(String text);

    Collection<ItemDto> getCommentsByItemId(long userId, long itemId);
}
