package json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.ShareItApplication;
import ru.yandex.practicum.item.CommentDto;
import ru.yandex.practicum.item.CommentResponseDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItApplication.class)
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Autowired
    private JacksonTester<CommentResponseDto> jsonD;

    @Test
    void testSerialize() throws Exception {
        CommentDto dto = CommentDto.builder()
                .text("Отличная вещь!")
                .build();

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("Отличная вещь!");
    }

    @Test
    void testDeserializeValid() throws Exception {
        String content = """
                {
                  "text": "Очень полезный комментарий"
                }
                """;

        CommentDto parsed = json.parseObject(content);

        assertThat(parsed.getText()).isEqualTo("Очень полезный комментарий");
    }

    @Test
    void testDeserializeInvalidBlankText() throws Exception {
        String content = """
                {
                  "text": ""
                }
                """;

        CommentDto parsed = json.parseObject(content);

        assertThat(parsed.getText()).isEmpty();
    }

    @Test
    void testSerialize_CommentResponseDto() throws Exception {
        CommentResponseDto dto = CommentResponseDto.builder()
                .id(1L)
                .text("Отличная вещь!")
                .authorName("Артём")
                .created(LocalDateTime.of(2026, 7, 9, 12, 30, 15))
                .build();

        JsonContent<CommentResponseDto> result = jsonD.write(dto);

        String expectedJson = """
                {
                  "id": 1,
                  "text": "Отличная вещь!",
                  "authorName": "Артём",
                  "created": "2026-07-09T12:30:15"
                }
                """;

        assertThat(result).isEqualToJson(expectedJson);
    }

    @Test
    void testDeserializeValid_CommentResponseDto() throws Exception {
        String content = """
                {
                  "id": 2,
                  "text": "Комментарий к вещи",
                  "authorName": "Иван",
                  "created": "2026-07-09T16:00:00"
                }
                """;

        CommentResponseDto parsed = jsonD.parseObject(content);

        assertThat(parsed.getId()).isEqualTo(2L);
        assertThat(parsed.getText()).isEqualTo("Комментарий к вещи");
        assertThat(parsed.getAuthorName()).isEqualTo("Иван");
        assertThat(parsed.getCreated()).isEqualTo(LocalDateTime.of(2026, 7, 9, 16, 0, 0));
    }
}
