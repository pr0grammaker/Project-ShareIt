package ru.yandex.practicum.item;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    CommentResponseDto mapToCommentResponseDto(Comment comment);

}
