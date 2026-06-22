package ru.yandex.practicum.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositoryDb extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

}
