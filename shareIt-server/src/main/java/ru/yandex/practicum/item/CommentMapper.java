package ru.yandex.practicum.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(source = "author.name", target = "authorName")
    CommentResponseDto mapToCommentResponseDto(Comment comment);

}
