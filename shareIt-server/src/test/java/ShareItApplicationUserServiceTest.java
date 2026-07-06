import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.ShareItApplication;
import ru.yandex.practicum.exceptions.DuplicatedDataException;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.item.ItemRepository;
import ru.yandex.practicum.user.*;

import java.util.Collection;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest(classes = ShareItApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ShareItApplicationUserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepositoryDb;

    @Autowired
    private ItemRepository itemRepositoryDb;

    @BeforeEach
    void clearDb() {
        itemRepositoryDb.deleteAll();
        userRepositoryDb.deleteAll();
    }

    private UserDto createTestUser(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);

        return userService.createUser(userDto);
    }

    @Test
    public void testGetAllUsers_Success() {
        createTestUser("Bob", "wefw@mail.com");
        createTestUser("Brown", "wwwfwff@mail.com");

        Collection<UserDto> users = userService.getAllUsers();

        assertThat(users)
                .isNotEmpty()
                .hasSize(2)
                .extracting("email")
                .containsExactlyInAnyOrder("wefw@mail.com", "wwwfwff@mail.com");
    }

    @Test
    public void testGetAllUsers_IsEmpty() {
        Collection<UserDto> users = userService.getAllUsers();

        assertThat(users)
                .hasSize(0)
                .isEmpty();
    }

    @Test
    public void createUser_Success() {
        UserDto user = createTestUser("Bob", "wefw@mail.com");

        assertThat(user)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Bob")
                .hasFieldOrPropertyWithValue("email", "wefw@mail.com");

        assertThat(user.getId()).isNotNull();
    }

    @Test
    public void createUser_EmailExist() {
        createTestUser("Bob", "wefw@mail.com");


        assertThatThrownBy(() -> createTestUser("Brown", "wefw@mail.com"))
                .isInstanceOf(DuplicatedDataException.class)
                .hasMessageContaining("email");
    }

    @Test
    void updateUser_UserNotFound() {
        UserDto dto = new UserDto();
        dto.setName("NewName");
        dto.setEmail("new@mail.com");

        assertThatThrownBy(() ->
                userService.updateUser(999999L, dto)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void updateUser_EmailAlreadyExists() {
        UserDto user1 = createTestUser("Bob", "bob@mail.com");
        createTestUser("Alex", "alex@mail.com");

        UserDto dto = new UserDto();
        dto.setEmail("alex@mail.com");

        assertThatThrownBy(() ->
                userService.updateUser(user1.getId(), dto)
        )
                .isInstanceOf(DuplicatedDataException.class)
                .hasMessageContaining("email");
    }

    @Test
    void updateUser_UpdateNameOnly() {
        UserDto user = createTestUser("Bob", "bob@mail.com");

        UserDto dto = new UserDto();
        dto.setName("UpdatedName");

        UserDto updated = userService.updateUser(user.getId(), dto);


        assertThat(updated)
                .extracting(UserDto::getName, UserDto::getEmail)
                .containsExactly("UpdatedName", "bob@mail.com");

    }

    @Test
    void updateUser_UpdateEmailOnly() {
        UserDto user = createTestUser("Bob", "bob@mail.com");

        UserDto dto = new UserDto();
        dto.setEmail("new@mail.com");

        UserDto updated = userService.updateUser(user.getId(), dto);

        assertThat(updated)
                .extracting(UserDto::getName, UserDto::getEmail)
                .containsExactly("Bob", "new@mail.com");
    }

    @Test
    void updateUser_UpdateAllFields() {
        UserDto user = createTestUser("Bob", "bob@mail.com");

        UserDto dto = new UserDto();
        dto.setName("Alice");
        dto.setEmail("alice@mail.com");

        UserDto updated = userService.updateUser(user.getId(), dto);

        assertThat(updated)
                .extracting(UserDto::getName, UserDto::getEmail)
                .containsExactly("Alice", "alice@mail.com");
    }

    @Test
    void updateUser_EmptyDto_NoChanges() {
        UserDto user = createTestUser("Bob", "bob@mail.com");

        UserDto dto = new UserDto();

        UserDto updated = userService.updateUser(user.getId(), dto);

        assertThat(updated)
                .extracting(UserDto::getName, UserDto::getEmail)
                .containsExactly("Bob", "bob@mail.com");
    }

    @Test
    void getUserById_Success() {
        UserDto user = createTestUser("Bob", "bob@mail.com");

        UserDto found = userService.getUserById(user.getId());

        assertThat(found)
                .isNotNull()
                .extracting(UserDto::getId, UserDto::getName, UserDto::getEmail)
                .containsExactly(user.getId(), "Bob", "bob@mail.com");
    }

    @Test
    void getUserById_NotFound() {
        assertThatThrownBy(() ->
                userService.getUserById(999L)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void deleteUser_Success() {
        UserDto user = createTestUser("Bob", "bob@mail.com");

        userService.deleteUser(user.getId());

        assertThatThrownBy(() ->
                userService.getUserById(user.getId())
        )
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteUser_NotFound() {
        assertThatThrownBy(() ->
                userService.deleteUser(999L)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }


}
