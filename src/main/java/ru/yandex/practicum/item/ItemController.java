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

    @PostMapping
    public ResponseEntity<Item> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.create(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Item> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable("itemId") long itemId,
                                           @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.create(userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItemById(@PathVariable("itemId") long itemId) {
        return ResponseEntity.ok().body(itemService.getItemById(itemId));
    }

    @GetMapping
    public ResponseEntity<Collection<Item>> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok().body(itemService.getItemsByOwner(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<Item>> searchItemsByText(@RequestParam(required = false, defaultValue = "") String text) {
        return ResponseEntity.ok().body(itemService.searchByText(text));
    }


}
