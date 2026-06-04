package ru.yandex.practicum.user;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long counterId = 1;

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User save(User user) {
        user.setId(counterId++);

        users.put(user.getId(), user);

        return user;
    }

    public Optional<User> findById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public User update(User user) {
        users.put(user.getId(), user);

        return user;
    }

    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    public boolean userEmailExist(String email){
        return users.values().stream()
                .anyMatch(user -> user.getEmail() != null
                        && user.getEmail().equalsIgnoreCase(email));
    }


}
