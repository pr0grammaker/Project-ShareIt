package ru.yandex.practicum.request;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemRequestMapper {

    ItemRequestResponseDto mapToItemItemRequestResponseDto(ItemRequest itemRequest);
}
