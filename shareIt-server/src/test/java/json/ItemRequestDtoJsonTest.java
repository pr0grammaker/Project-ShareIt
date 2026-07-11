package json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.ShareItApplication;
import ru.yandex.practicum.request.ItemRequestDto;
import ru.yandex.practicum.request.ItemRequestResponseDto;
import ru.yandex.practicum.request.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItApplication.class)
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Autowired
    private JacksonTester<ItemResponseDto> jsonD;

    @Autowired
    private JacksonTester<ItemRequestResponseDto> jsonT;

    @Test
    void testSerialize_ItemRequestDto() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .description("Нужна дрель для ремонта")
                .build();

        JsonContent<ItemRequestDto> result = json.write(dto);

        String expectedJson = """
            {
              "description": "Нужна дрель для ремонта"
            }
            """;

        assertThat(result).isEqualToJson(expectedJson);
    }

    @Test
    void testDeserialize_ItemRequestDto() throws Exception {
        String content = """
            {
              "description": "Хочу взять велосипед"
            }
            """;

        ItemRequestDto parsed = json.parseObject(content);

        assertThat(parsed.getDescription()).isEqualTo("Хочу взять велосипед");
    }

    @Test
    void testSerialize_ItemResponseDto() throws Exception {
        ItemResponseDto dto = ItemResponseDto.builder()
                .id(1L)
                .ownerId(23L)
                .name("Дрель")
                .build();

        JsonContent<ItemResponseDto> result = jsonD.write(dto);

        String expectedJson = """
            {
              "id": 1,
              "ownerId": 23,
              "name": "Дрель"
            }
            """;

        assertThat(result).isEqualToJson(expectedJson);
    }

    @Test
    void testDeserialize_ItemResponseDto() throws Exception {
        String content = """
            {
              "id": 2,
              "ownerId": 99,
              "name": "Велосипед"
            }
            """;

        ItemResponseDto parsed = jsonD.parseObject(content);

        assertThat(parsed.getId()).isEqualTo(2L);
        assertThat(parsed.getOwnerId()).isEqualTo(99L);
        assertThat(parsed.getName()).isEqualTo("Велосипед");
    }

    @Test
    void testSerialize_ItemRequestResponseDto() throws Exception {
        ItemResponseDto item = ItemResponseDto.builder()
                .id(5L)
                .ownerId(23L)
                .name("Дрель")
                .build();

        ItemRequestResponseDto dto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("Нужна дрель для ремонта")
                .created(LocalDateTime.of(2026, 7, 9, 12, 30, 15))
                .items(List.of(item))
                .build();

        JsonContent<ItemRequestResponseDto> result = jsonT.write(dto);

        String expectedJson = """
            {
              "id": 1,
              "description": "Нужна дрель для ремонта",
              "created": "2026-07-09T12:30:15",
              "items": [
                {
                  "id": 5,
                  "ownerId": 23,
                  "name": "Дрель"
                }
              ]
            }
            """;

        assertThat(result).isEqualToJson(expectedJson);
    }

    @Test
    void testDeserialize_ItemRequestResponseDto() throws Exception {
        String content = """
            {
              "id": 2,
              "description": "Хочу взять велосипед",
              "created": "2026-07-09T16:00:00",
              "items": [
                {
                  "id": 7,
                  "ownerId": 99,
                  "name": "Велосипед"
                }
              ]
            }
            """;

        ItemRequestResponseDto parsed = jsonT.parseObject(content);

        assertThat(parsed.getId()).isEqualTo(2L);
        assertThat(parsed.getDescription()).isEqualTo("Хочу взять велосипед");
        assertThat(parsed.getCreated()).isEqualTo(LocalDateTime.of(2026, 7, 9, 16, 0, 0));
        assertThat(parsed.getItems()).hasSize(1);
        assertThat(parsed.getItems().get(0).getId()).isEqualTo(7L);
        assertThat(parsed.getItems().get(0).getOwnerId()).isEqualTo(99L);
        assertThat(parsed.getItems().get(0).getName()).isEqualTo("Велосипед");
    }




}