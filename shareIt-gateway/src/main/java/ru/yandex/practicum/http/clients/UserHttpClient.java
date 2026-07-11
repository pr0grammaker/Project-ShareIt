package ru.yandex.practicum.http.clients;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.*;
import ru.yandex.practicum.user.UserDto;

import java.util.Collection;

@HttpExchange(
        accept = "application/json",
        contentType = "application/json",
        url = "/users"
)
public interface UserHttpClient {

    @GetExchange
    ResponseEntity<Collection<UserDto>> getAllUsers();

    @GetExchange("/{userId}")
    ResponseEntity<UserDto> getUserById(@PathVariable("userId") long userId);

    @PostExchange
    ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto);

    @PatchExchange("/{userId}")
    ResponseEntity<UserDto> updateUser(@PathVariable("userId") long userId,
                                       @RequestBody UserDto userDto);

    @DeleteExchange("/{userId}")
    ResponseEntity<Void> deleteUser(@PathVariable("userId") long userId);
}
