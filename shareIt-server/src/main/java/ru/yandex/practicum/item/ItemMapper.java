package ru.yandex.practicum.item;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    ItemDto mapToItemDto(Item item);

    @Mapping(source = "ownerId", target = "owner.id")
    Item mapToItem(ItemDto dto);
}
