package json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.ShareItApplication;
import ru.yandex.practicum.item.ItemDto;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItApplication.class)
public class ItemDtoJsonTest {

    @Autowired
    JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .ownerId(23L)
                .name("Название")
                .description("Описание")
                .available(true)
                .requestId(2L)
                .comments(new ArrayList<>())
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        String expectedJson = """
                {
                  "id": 1,
                  "ownerId": 23,
                  "name": "Название",
                  "description": "Описание",
                  "available": true,
                  "requestId": 2,
                  "comments": []
                }
                """;

        assertThat(result).isEqualToJson(expectedJson);
    }

    @Test
    void testDeserialize() throws Exception {
        String content = """
            {
              "id": 1,
              "ownerId": 23,
              "name": "Название",
              "description": "Описание",
              "available": true,
              "requestId": 2,
              "comments": []
            }
            """;

        ItemDto parsed = json.parseObject(content);

        assertThat(parsed.getId()).isEqualTo(1L);
        assertThat(parsed.getOwnerId()).isEqualTo(23L);
        assertThat(parsed.getName()).isEqualTo("Название");
        assertThat(parsed.getDescription()).isEqualTo("Описание");
        assertThat(parsed.getAvailable()).isTrue();
        assertThat(parsed.getRequestId()).isEqualTo(2L);
        assertThat(parsed.getComments()).isEmpty();
    }
}
