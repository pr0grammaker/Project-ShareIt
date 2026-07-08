package ru.yandex.practicum.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDto {
    @NotBlank(message = "Текст комментария обязателен")
    private String text;
}

