import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.ShareItApplication;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.item.ItemDto;
import ru.yandex.practicum.item.ItemRepository;
import ru.yandex.practicum.item.ItemService;
import ru.yandex.practicum.user.UserDto;
import ru.yandex.practicum.user.UserRepository;
import ru.yandex.practicum.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = ShareItApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ShareItApplicationItemServiceTest {

    @Autowired
    private ItemService itemService;

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

    private ItemDto createItem(long userId, String name, String description, boolean available) {
        ItemDto itemDto = ItemDto.builder()
                .name(name)
                .description(description)
                .available(available)
                .build();

        return itemService.create(userId, itemDto);
    }

    @Test
    void createItem_Success() {
        UserDto user = createTestUser("Bob", "bob@mail.com");

        ItemDto item = createItem(
                user.getId(),
                "Item1",
                "Desc1",
                true
        );

        assertThat(item)
                .isNotNull()
                .extracting(
                        ItemDto::getName,
                        ItemDto::getDescription,
                        ItemDto::getAvailable
                )
                .containsExactly(
                        "Item1",
                        "Desc1",
                        true
                );

        assertThat(item.getId()).isPositive();
    }

    @Test
    void createItem_UserNotFound() {
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Desc1")
                .available(true)
                .build();

        assertThatThrownBy(() ->
                itemService.create(999L, itemDto)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void createItem_OwnerIsSetCorrectly() {
        UserDto user = createTestUser("Bob", "bob@mail.com");

        ItemDto item = createItem(
                user.getId(),
                "Item1",
                "Desc1",
                true
        );

        assertThat(item.getOwnerId()).isEqualTo(user.getId());
    }

    @Test
    void createItem_MapsDtoCorrectly() {
        UserDto user = createTestUser("Bob", "bob@mail.com");

        ItemDto dto = ItemDto.builder()
                .name("Phone")
                .description("iPhone 15")
                .available(true)
                .build();

        ItemDto item = itemService.create(user.getId(), dto);

        assertThat(item)
                .extracting(ItemDto::getName, ItemDto::getDescription, ItemDto::getAvailable)
                .containsExactly("Phone", "iPhone 15", true);
    }

    @Test
    void createItem_IdIsGenerated() {
        UserDto user = createTestUser("Bob", "bob@mail.com");

        ItemDto item = createItem(
                user.getId(),
                "Item1",
                "Desc1",
                true
        );

        assertThat(item.getId())
                .isNotNull()
                .isPositive();
    }

    @Test
    void updateItem_Success_FullUpdate() {
        UserDto user = createTestUser("Bob", "bob@mail.com");
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Desc1")
                .available(true)
                .build();

        ItemDto item = itemService.create(user.getId(), itemDto);

        ItemDto dto = ItemDto.builder()
                .name("Updated")
                .description("Updated desc")
                .available(false)
                .build();

        ItemDto updated = itemService.update(user.getId(), item.getId(), dto);

        assertThat(updated)
                .isNotNull()
                .extracting(
                        ItemDto::getName,
                        ItemDto::getDescription,
                        ItemDto::getAvailable,
                        ItemDto::getOwnerId
                )
                .containsExactly(
                        "Updated",
                        "Updated desc",
                        false,
                        user.getId()
                );
    }

    @Test
    void updateItem_UpdateNameOnly() {
        UserDto user = createTestUser("Bob", "bob@mail.com");
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Desc1")
                .available(true)
                .build();

        ItemDto item = itemService.create(user.getId(), itemDto);

        ItemDto dto = ItemDto.builder()
                .name("NewName")
                .build();

        ItemDto updated = itemService.update(user.getId(), item.getId(), dto);

        assertThat(updated)
                .extracting(
                        ItemDto::getName,
                        ItemDto::getDescription,
                        ItemDto::getAvailable
                )
                .containsExactly(
                        "NewName",
                        "Desc1",
                        true
                );
    }

    @Test
    void updateItem_UpdateDescriptionOnly() {
        UserDto user = createTestUser("Bob", "bob@mail.com");
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Desc1")
                .available(true)
                .build();

        ItemDto item = itemService.create(user.getId(), itemDto);

        ItemDto dto = ItemDto.builder()
                .description("NewDesc")
                .build();

        ItemDto updated = itemService.update(user.getId(), item.getId(), dto);

        assertThat(updated)
                .extracting(
                        ItemDto::getName,
                        ItemDto::getDescription,
                        ItemDto::getAvailable
                )
                .containsExactly(
                        "Item1",
                        "NewDesc",
                        true
                );
    }

    @Test
    void updateItem_ItemNotFound() {
        UserDto user = createTestUser("Bob", "bob@mail.com");
        ItemDto dto = ItemDto.builder()
                .name("Updated")
                .description("Updated desc")
                .available(false)
                .build();

        assertThatThrownBy(() ->
                itemService.update(user.getId(), 999L, dto)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь не найдена");
    }

    @Test
    void updateItem_UserNotFound() {
        UserDto user = createTestUser("Bob", "bob@mail.com");
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Desc1")
                .available(true)
                .build();

        ItemDto item = itemService.create(user.getId(), itemDto);

        ItemDto dto = ItemDto.builder()
                .name("Updated")
                .description("Updated desc")
                .available(false)
                .build();

        assertThatThrownBy(() ->
                itemService.update(999L, item.getId(), dto)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void updateItem_NotOwner() {
        UserDto user = createTestUser("Bob", "bob@mail.com");
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Desc1")
                .available(true)
                .build();

        ItemDto item = itemService.create(user.getId(), itemDto);

        ItemDto dto = ItemDto.builder()
                .name("Updated")
                .description("Updated desc")
                .available(false)
                .build();

        UserDto anotherUser = createTestUser("Alex", "alex@mail.com");

        assertThatThrownBy(() ->
                itemService.update(anotherUser.getId(), item.getId(), dto)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не является владельцем вещи");
    }

    @Test
    void getItemById_Success() {
        UserDto user = createTestUser("Bob", "bob@mail.com");
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Desc1")
                .available(true)
                .build();

        ItemDto item = itemService.create(user.getId(), itemDto);

        ItemDto found = itemService.getItemById(item.getId());

        assertThat(found)
                .isNotNull()
                .extracting(
                        ItemDto::getName,
                        ItemDto::getDescription,
                        ItemDto::getAvailable,
                        ItemDto::getOwnerId
                )
                .containsExactly(
                        "Item1",
                        "Desc1",
                        true,
                        user.getId()
                );
    }

    @Test
    void getItemById_NotFound() {
        assertThatThrownBy(() ->
                itemService.getItemById(999L)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь не найдена");
    }

    @Test
    void getItemsByOwner_Success() {
        UserDto user = createTestUser("Bob", "bob@mail.com");

        createItem(user.getId(), "Item1", "Desc1", true);
        createItem(user.getId(), "Item2", "Desc2", false);

        var items = itemService.getItemsByOwner(user.getId());

        assertThat(items)
                .isNotEmpty()
                .hasSize(2)
                .extracting(ItemDto::getName)
                .containsExactlyInAnyOrder("Item1", "Item2");
    }

    @Test
    void getItemsByOwner_EmptyList() {
        UserDto user = createTestUser("Bob", "bob@mail.com");

        var items = itemService.getItemsByOwner(user.getId());

        assertThat(items)
                .isEmpty();
    }

    @Test
    void getItemsByOwner_FilterByOwner() {
        UserDto user1 = createTestUser("Bob", "bob@mail.com");
        UserDto user2 = createTestUser("Alex", "alex@mail.com");

        createItem(user1.getId(), "Item1", "Desc1", true);
        createItem(user2.getId(), "Item2", "Desc2", true);
        createItem(user1.getId(), "Item3", "Desc3", true);

        var items = itemService.getItemsByOwner(user1.getId());

        assertThat(items)
                .hasSize(2)
                .extracting(ItemDto::getName)
                .containsExactlyInAnyOrder("Item1", "Item3");
    }

    @Test
    void getItemsByOwner_OwnerIdCorrect() {
        UserDto user = createTestUser("Bob", "bob@mail.com");

        createItem(user.getId(), "Item1", "Desc1", true);

        var items = itemService.getItemsByOwner(user.getId());

        assertThat(items)
                .isNotEmpty()
                .extracting(ItemDto::getOwnerId)
                .containsOnly(user.getId());
    }

    @Test
    void searchByText_Null_ReturnEmpty() {
        var result = itemService.searchByText(null);

        assertThat(result).isEmpty();
    }

    @Test
    void searchByText_Blank_ReturnEmpty() {
        var result = itemService.searchByText("   ");

        assertThat(result).isEmpty();
    }

    @Test
    void searchByText_FoundItems() {
        UserDto user = createTestUser("Bob", "bob@mail.com");

        createItem(user.getId(), "Drill", "Power tool", true);
        createItem(user.getId(), "Hammer", "Steel tool", true);

        var result = itemService.searchByText("drill");

        assertThat(result)
                .isNotEmpty()
                .extracting(ItemDto::getName)
                .contains("Drill");
    }

    @Test
    void searchByText_CaseInsensitive() {
        UserDto user = createTestUser("Bob", "bob@mail.com");

        createItem(user.getId(), "DRILL", "Power tool", true);

        var result = itemService.searchByText("drill");

        assertThat(result)
                .isNotEmpty()
                .extracting(ItemDto::getName)
                .contains("DRILL");
    }


}
