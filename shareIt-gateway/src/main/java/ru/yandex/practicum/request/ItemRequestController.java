//package ru.yandex.practicum.request;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Collection;
//
//@RestController
//@RequestMapping("/requests")
//@RequiredArgsConstructor
//public class ItemRequestController {
//    private final ItemRequestService itemRequestService;
//
//    @PostMapping
//    public ResponseEntity<ItemRequestResponseDto> create(
//            @RequestHeader("X-Sharer-User-Id") long userId,
//            @Valid @RequestBody ItemRequestDto itemRequestDto
//    ) {
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(itemRequestService.create(userId, itemRequestDto));
//    }
//
//    @GetMapping
//    public ResponseEntity<Collection<ItemRequestResponseDto>> getByUserId(
//            @RequestHeader("X-Sharer-User-Id") long userId
//    ) {
//        return ResponseEntity.ok().body(itemRequestService.get(userId));
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<Collection<ItemRequestResponseDto>> getAll() {
//        return ResponseEntity.ok().body(itemRequestService.getAll());
//    }
//
//    @GetMapping("/{requestId}")
//    public ResponseEntity<ItemRequestResponseDto> getRequestById(
//            @RequestHeader("X-Sharer-User-Id") long userId
//    ) {
//        return ResponseEntity.ok().body(itemRequestService.getById(userId));
//    }
//
//}
