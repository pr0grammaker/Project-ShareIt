package ru.yandex.practicum.http.clients;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import ru.yandex.practicum.request.ItemRequestDto;
import ru.yandex.practicum.request.ItemRequestResponseDto;

import java.util.Collection;

@HttpExchange(
        accept = "application/json",
        contentType = "application/json",
        url = "/requests"
)
public interface ItemRequestHttpClient {

    @PostExchange
    ResponseEntity<ItemRequestResponseDto> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto);

    @GetExchange
    ResponseEntity<Collection<ItemRequestResponseDto>> getByUserId(
            @RequestHeader("X-Sharer-User-Id") long userId);

    @GetExchange("/all")
    ResponseEntity<Collection<ItemRequestResponseDto>> getAll();

    @GetExchange("/{requestId}")
    ResponseEntity<ItemRequestResponseDto> getRequestById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("requestId") long requestId);
}
