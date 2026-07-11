package ru.yandex.practicum.item;

public interface CommentService {

    CommentResponseDto createComment(long userId, long itemId, CommentDto commentDto);
}
