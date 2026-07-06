package ru.yandex.practicum.request;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestResponseDto create(long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestResponseDto> get(long userId);

    Collection<ItemRequestResponseDto> getAll();

    ItemRequestResponseDto getById(long userId);
}
