package ru.yandex.practicum.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.create(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("itemId") long itemId,
            @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.update(userId, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(
            @PathVariable("itemId") long itemId) {
        return ResponseEntity.ok().body(itemService.getItemById(itemId));
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> getAllItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok().body(itemService.getItemsByOwner(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> searchItemsByText(
            @RequestParam(required = false, defaultValue = "") String text) {
        return ResponseEntity.ok().body(itemService.searchByText(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> createComment(
                    @RequestHeader("X-Sharer-User-Id") long userId,
                    @PathVariable("itemId") long itemId,
                    @Valid @RequestBody CommentDto commentDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(userId, itemId, commentDto));
    }

    @GetMapping("/{itemId}/comments")
    public ResponseEntity<Collection<ItemDto>> getCommentsByItemId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("itemId") long itemId
    ){
        return ResponseEntity.ok().body(itemService.getCommentsByItemId(userId, itemId));
    }


}
