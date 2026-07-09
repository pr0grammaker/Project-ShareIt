package ru.yandex.practicum.user;

import java.util.Collection;

public interface UserService {

    Collection<User> getAllUsers();

    User createUser(UserDto userDto);

    User updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);

    User getUserById(Long userId);

}
