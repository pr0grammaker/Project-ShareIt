package json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.ShareItApplication;
import ru.yandex.practicum.booking.BookingDto;
import ru.yandex.practicum.booking.BookingResponseDto;
import ru.yandex.practicum.enums.Status;
import ru.yandex.practicum.item.ItemDto;
import ru.yandex.practicum.user.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItApplication.class)
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Autowired
    private JacksonTester<BookingResponseDto> jsonD;


    @Test
    void testSerialize_BookingDto() throws Exception {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .bookingStart(LocalDateTime.of(2026, 7, 10, 14, 0, 0))
                .bookingEnd(LocalDateTime.of(2026, 7, 12, 18, 30, 0))
                .itemId(5L)
                .build();

        JsonContent<BookingDto> result = json.write(dto);

        String expectedJson = """
                {
                  "id": 1,
                  "bookingStart": "2026-07-10T14:00:00",
                  "bookingEnd": "2026-07-12T18:30:00",
                  "itemId": 5
                }
                """;

        assertThat(result).isEqualToJson(expectedJson);
    }

    @Test
    void testDeserialize_BookingDto() throws Exception {
        String content = """
                {
                  "id": 2,
                  "bookingStart": "2026-07-15T09:00:00",
                  "bookingEnd": "2026-07-16T11:00:00",
                  "itemId": 10
                }
                """;

        BookingDto parsed = json.parseObject(content);

        assertThat(parsed.getId()).isEqualTo(2L);
        assertThat(parsed.getBookingStart()).isEqualTo(LocalDateTime.of(2026, 7, 15, 9, 0, 0));
        assertThat(parsed.getBookingEnd()).isEqualTo(LocalDateTime.of(2026, 7, 16, 11, 0, 0));
        assertThat(parsed.getItemId()).isEqualTo(10L);
    }

    @Test
    void testSerialize_BookingResponseDto() throws Exception {
        ItemDto item = ItemDto.builder()
                .id(5L)
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .build();

        UserDto booker = UserDto.builder()
                .id(10L)
                .name("Артём")
                .email("artem@example.com")
                .build();

        BookingResponseDto dto = BookingResponseDto.builder()
                .id(1L)
                .bookingStart(LocalDateTime.of(2026, 7, 10, 14, 0, 0))
                .bookingEnd(LocalDateTime.of(2026, 7, 12, 18, 30, 0))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        JsonContent<BookingResponseDto> result = jsonD.write(dto);

        String expectedJson = """
                {
                  "id": 1,
                  "bookingStart": "2026-07-10T14:00:00",
                  "bookingEnd": "2026-07-12T18:30:00",
                  "item": {
                    "id": 5,
                    "name": "Вещь",
                    "description": "Описание вещи",
                    "available": true
                  },
                  "booker": {
                    "id": 10,
                    "name": "Артём",
                    "email": "artem@example.com"
                  },
                  "status": "APPROVED"
                }
                """;

        assertThat(result).isEqualToJson(expectedJson);
    }

    @Test
    void testDeserialize_BookingResponseDto() throws Exception {
        String content = """
                {
                  "id": 2,
                  "bookingStart": "2026-07-15T09:00:00",
                  "bookingEnd": "2026-07-16T11:00:00",
                  "item": {
                    "id": 7,
                    "name": "Ноутбук",
                    "description": "Игровой ноутбук",
                    "available": false
                  },
                  "booker": {
                    "id": 20,
                    "name": "Иван",
                    "email": "ivan@example.com"
                  },
                  "status": "REJECTED"
                }
                """;

        BookingResponseDto parsed = jsonD.parseObject(content);

        assertThat(parsed.getId()).isEqualTo(2L);
        assertThat(parsed.getBookingStart()).isEqualTo(LocalDateTime.of(2026, 7, 15, 9, 0, 0));
        assertThat(parsed.getBookingEnd()).isEqualTo(LocalDateTime.of(2026, 7, 16, 11, 0, 0));
        assertThat(parsed.getItem().getId()).isEqualTo(7L);
        assertThat(parsed.getItem().getName()).isEqualTo("Ноутбук");
        assertThat(parsed.getItem().getDescription()).isEqualTo("Игровой ноутбук");
        assertThat(parsed.getItem().getAvailable()).isFalse();
        assertThat(parsed.getBooker().getId()).isEqualTo(20L);
        assertThat(parsed.getBooker().getName()).isEqualTo("Иван");
        assertThat(parsed.getBooker().getEmail()).isEqualTo("ivan@example.com");
        assertThat(parsed.getStatus()).isEqualTo(Status.REJECTED);
    }


}