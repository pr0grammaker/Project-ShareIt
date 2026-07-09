package ru.yandex.practicum.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.booking.BookingResponseDto;

import java.util.List;

@Data
@Builder
public class ItemDto {

    private Long id;

    private Long ownerId;

    @NotBlank(message = "Название вещи не может быть пустым")
    @Size(max = 100, message = "Название не должно превышать 100 символов")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

    @NotNull(message = "Доступность должна быть указана")
    private Boolean available;

    private BookingResponseDto lastBooking;
    private BookingResponseDto nextBooking;

    private Long requestId;

    private List<CommentResponseDto> comments;

}
