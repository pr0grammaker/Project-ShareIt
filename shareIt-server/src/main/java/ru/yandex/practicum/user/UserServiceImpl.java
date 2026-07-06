package ru.yandex.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exceptions.DuplicatedDataException;
import ru.yandex.practicum.exceptions.NotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepositoryDb;
    private final UserMapper userMapper;

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepositoryDb.findAll().stream()
                .map(userMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepositoryDb.existsByEmail(userDto.getEmail())) {
            throw new DuplicatedDataException("Пользователь с таким email уже существует");
        }
        User user = userMapper.mapToUser(userDto);

        User save = userRepositoryDb.save(user);

        return userMapper.mapToUserDto(save);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = userRepositoryDb.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userDto.getEmail() != null &&
                userRepositoryDb.existsByEmail(userDto.getEmail()) &&
                !user.getEmail().equals(userDto.getEmail())) {
            throw new DuplicatedDataException("Пользователь с таким email уже существует");
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        User save = userRepositoryDb.save(user);
        return userMapper.mapToUserDto(save);
    }

    @Override
    public void deleteUser(long userId) {
        userRepositoryDb.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        userRepositoryDb.deleteById(userId);
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = userRepositoryDb.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return userMapper.mapToUserDto(user);
    }
}
