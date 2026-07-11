package ru.yandex.practicum.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.http.clients.ItemRequestHttpClient;

import java.util.Collection;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestHttpClient itemRequestHttpClient;

    @PostMapping
    public ResponseEntity<ItemRequestResponseDto> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestHttpClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemRequestResponseDto>> getByUserId(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestHttpClient.getByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<ItemRequestResponseDto>> getAll() {
        return itemRequestHttpClient.getAll();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestResponseDto> getRequestById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("requestId") long requestId) {
        return itemRequestHttpClient.getRequestById(userId, requestId);
    }

}
