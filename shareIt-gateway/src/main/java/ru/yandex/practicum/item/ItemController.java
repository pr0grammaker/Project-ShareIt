package ru.yandex.practicum.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.http.clients.CommentHttpClient;
import ru.yandex.practicum.http.clients.ItemHttpClient;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemHttpClient itemHttpClient;
    private final CommentHttpClient commentHttpClient;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemDto itemDto) {
        return itemHttpClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("itemId") long itemId,
            @RequestBody ItemDto itemDto) {
        return itemHttpClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(
            @PathVariable("itemId") long itemId) {
        return itemHttpClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> getAllItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemHttpClient.getAllItemsByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> searchItemsByText(
            @RequestParam(required = false, defaultValue = "") String text) {
        return itemHttpClient.searchItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> createComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("itemId") long itemId,
            @Valid @RequestBody CommentDto commentDto) {
        return commentHttpClient.createComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}/comments")
    public ResponseEntity<Collection<ItemDto>> getCommentsByItemId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("itemId") long itemId
    ) {
        return itemHttpClient.getCommentsByItemId(userId, itemId);
    }


}
