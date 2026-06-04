package ru.yandex.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.expection.DuplicatedDataException;
import ru.yandex.practicum.expection.NotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User createUser(UserDto userDto) {
        if (userRepository.userEmailExist(userDto.getEmail())) {
            throw new DuplicatedDataException(
                    "Пользователь с таким email уже существует");
        }

        User user = UserMapper.mapToUser(userDto);

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (userDto.getEmail() != null &&
                userRepository.userEmailExist(userDto.getEmail()) &&
                !user.getEmail().equals(userDto.getEmail())) {
            throw new DuplicatedDataException(
                    "Пользователь с таким email уже существует");
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        return userRepository.update(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userRepository.deleteUser(userId);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
