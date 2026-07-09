package ru.yandex.practicum.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым!", groups = OnCreate.class)
    private String name;

    @NotBlank(message = "Email не может быть пустым", groups = OnCreate.class)
    @Email(message = "Введенный email не соответствует формату email-адресов!", groups = {OnCreate.class, OnUpdate.class})
    private String email;
}
