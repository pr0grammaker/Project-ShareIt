package ru.yandex.practicum.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.http.clients.UserHttpClient;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserHttpClient userHttpClient;

    @GetMapping
    public ResponseEntity<Collection<UserDto>> getAllUsers() {
        return ResponseEntity.ok().body(userHttpClient.getAllUsers().getBody());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("userId") long userId) {
        return ResponseEntity.ok().body(userHttpClient.getUserById(userId).getBody());
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userHttpClient.createUser(userDto).getBody());
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("userId") long userId,
                                              @RequestBody UserDto userDto) {
        return ResponseEntity.ok().body(userHttpClient.updateUser(userId, userDto).getBody());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") long userId) {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(userHttpClient.deleteUser(userId).getBody());
    }


}
