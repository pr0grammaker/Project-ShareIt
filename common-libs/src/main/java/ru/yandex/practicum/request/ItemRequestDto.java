package ru.yandex.practicum.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemRequestDto {

    @NotBlank(message = "Название вещи не может быть пустым")
    @Size(max = 1000, message = "Название не должно превышать 100 символов")
    private String description;
}
