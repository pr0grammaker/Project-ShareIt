package ru.yandex.practicum.http.clients;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import ru.yandex.practicum.item.CommentDto;
import ru.yandex.practicum.item.CommentResponseDto;

@HttpExchange(
        accept = "application/json",
        contentType = "application/json",
        url = "/items"
)
public interface CommentHttpClient {

    @PostExchange("/{itemId}/comment")
    ResponseEntity<CommentResponseDto> createComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("itemId") long itemId,
            @Valid @RequestBody CommentDto commentDto);
}
