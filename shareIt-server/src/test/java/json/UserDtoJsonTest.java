package json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.ShareItApplication;
import ru.yandex.practicum.user.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItApplication.class)
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Артём")
                .email("artem@example.com")
                .build();

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Артём");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("artem@example.com");
    }

    @Test
    void testDeserializeValid() throws Exception {
        String content = """
                {
                  "id": 2,
                  "name": "Иван",
                  "email": "ivan@example.com"
                }
                """;

        UserDto parsed = json.parseObject(content);

        assertThat(parsed.getId()).isEqualTo(2L);
        assertThat(parsed.getName()).isEqualTo("Иван");
        assertThat(parsed.getEmail()).isEqualTo("ivan@example.com");
    }

    @Test
    void testDeserializeInvalidEmail() throws Exception {
        String content = """
                {
                  "id": 3,
                  "name": "Петр",
                  "email": "not-an-email"
                }
                """;

        UserDto parsed = json.parseObject(content);

        assertThat(parsed.getEmail()).isEqualTo("not-an-email");
    }
}
