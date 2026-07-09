package ru.yandex.practicum.http.clients;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;
import ru.yandex.practicum.item.ItemDto;

import java.util.Collection;

@HttpExchange(
        accept = "application/json",
        contentType = "application/json",
        url = "/items"
)
public interface ItemHttpClient {

    @PostExchange
    ResponseEntity<ItemDto> addItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemDto itemDto);

    @PatchExchange("/{itemId}")
    ResponseEntity<ItemDto> updateItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("itemId") long itemId,
            @RequestBody ItemDto itemDto);

    @GetExchange("/{itemId}")
    ResponseEntity<ItemDto> getItemById(
            @PathVariable("itemId") long itemId);

    @GetExchange
    ResponseEntity<Collection<ItemDto>> getAllItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") long userId);

    @GetExchange("/search")
    ResponseEntity<Collection<ItemDto>> searchItemsByText(
            @RequestParam(required = false, defaultValue = "") String text);


    @GetExchange("/{itemId}/comments")
    ResponseEntity<Collection<ItemDto>> getCommentsByItemId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("itemId") long itemId);
}
