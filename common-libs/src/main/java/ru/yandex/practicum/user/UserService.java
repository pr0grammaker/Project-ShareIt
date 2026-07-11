package ru.yandex.practicum.user;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    void deleteUser(long userId);

    UserDto getUserById(long userId);

}
