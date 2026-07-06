package ru.yandex.practicum.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым!")
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Введенный email не соответствует формату email-адресов!")
    private String email;
}
