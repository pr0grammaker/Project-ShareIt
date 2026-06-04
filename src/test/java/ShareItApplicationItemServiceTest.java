import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.ShareItApplication;
import ru.yandex.practicum.expection.NotFoundException;
import ru.yandex.practicum.item.Item;
import ru.yandex.practicum.item.ItemDto;
import ru.yandex.practicum.item.ItemService;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.UserDto;
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

    private User createTestUser(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);

        return userService.createUser(userDto);
    }

    private Item createItem(long userId, String name, String description, boolean available) {
        ItemDto itemDto = ItemDto.builder()
                .name(name)
                .description(description)
                .available(available)
                .build();

        return itemService.create(userId, itemDto);
    }

    @Test
    void createItem_Success() {
        User user = createTestUser("Bob", "bob@mail.com");

        Item item = createItem(
                user.getId(),
                "Item1",
                "Desc1",
                true
        );

        assertThat(item)
                .isNotNull()
                .extracting(
                        Item::getName,
                        Item::getDescription,
                        Item::isAvailable,
                        Item::getOwnerId
                )
                .containsExactly(
                        "Item1",
                        "Desc1",
                        true,
                        user.getId()
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
                .hasMessageContaining("User not found");
    }

    @Test
    void createItem_OwnerIsSetCorrectly() {
        User user = createTestUser("Bob", "bob@mail.com");

        Item item = createItem(
                user.getId(),
                "Item1",
                "Desc1",
                true
        );

        assertThat(item.getOwnerId()).isEqualTo(user.getId());
    }

    @Test
    void createItem_MapsDtoCorrectly() {
        User user = createTestUser("Bob", "bob@mail.com");

        ItemDto dto = ItemDto.builder()
                .name("Phone")
                .description("iPhone 15")
                .available(true)
                .build();

        Item item = itemService.create(user.getId(), dto);

        assertThat(item)
                .extracting(Item::getName, Item::getDescription, Item::isAvailable)
                .containsExactly("Phone", "iPhone 15", true);
    }

    @Test
    void createItem_IdIsGenerated() {
        User user = createTestUser("Bob", "bob@mail.com");

        Item item = createItem(
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
        User user = createTestUser("Bob", "bob@mail.com");
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Desc1")
                .available(true)
                .build();

        Item item = itemService.create(user.getId(), itemDto);

        ItemDto dto = ItemDto.builder()
                .name("Updated")
                .description("Updated desc")
                .available(false)
                .build();

        Item updated = itemService.update(user.getId(), item.getId(), dto);

        assertThat(updated)
                .isNotNull()
                .extracting(
                        Item::getName,
                        Item::getDescription,
                        Item::isAvailable,
                        Item::getOwnerId
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
        User user = createTestUser("Bob", "bob@mail.com");
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Desc1")
                .available(true)
                .build();

        Item item = itemService.create(user.getId(), itemDto);

        ItemDto dto = ItemDto.builder()
                .name("NewName")
                .build();

        Item updated = itemService.update(user.getId(), item.getId(), dto);

        assertThat(updated)
                .extracting(
                        Item::getName,
                        Item::getDescription,
                        Item::isAvailable
                )
                .containsExactly(
                        "NewName",
                        "Desc1",
                        true
                );
    }

    @Test
    void updateItem_UpdateDescriptionOnly() {
        User user = createTestUser("Bob", "bob@mail.com");
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Desc1")
                .available(true)
                .build();

        Item item = itemService.create(user.getId(), itemDto);

        ItemDto dto = ItemDto.builder()
                .description("NewDesc")
                .build();

        Item updated = itemService.update(user.getId(), item.getId(), dto);

        assertThat(updated)
                .extracting(
                        Item::getName,
                        Item::getDescription,
                        Item::isAvailable
                )
                .containsExactly(
                        "Item1",
                        "NewDesc",
                        true
                );
    }

    @Test
    void updateItem_ItemNotFound() {
        User user = createTestUser("Bob", "bob@mail.com");
        ItemDto dto = ItemDto.builder()
                .name("Updated")
                .description("Updated desc")
                .available(false)
                .build();

        assertThatThrownBy(() ->
                itemService.update(user.getId(), 999L, dto)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Item not found");
    }

    @Test
    void updateItem_UserNotFound() {
        User user = createTestUser("Bob", "bob@mail.com");
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Desc1")
                .available(true)
                .build();

        Item item = itemService.create(user.getId(), itemDto);

        ItemDto dto = ItemDto.builder()
                .name("Updated")
                .description("Updated desc")
                .available(false)
                .build();

        assertThatThrownBy(() ->
                itemService.update(999L, item.getId(), dto)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void updateItem_NotOwner() {
        User user = createTestUser("Bob", "bob@mail.com");
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Desc1")
                .available(true)
                .build();

        Item item = itemService.create(user.getId(), itemDto);

        ItemDto dto = ItemDto.builder()
                .name("Updated")
                .description("Updated desc")
                .available(false)
                .build();

        User anotherUser = createTestUser("Alex", "alex@mail.com");

        assertThatThrownBy(() ->
                itemService.update(anotherUser.getId(), item.getId(), dto)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User is not owner of item");
    }

    @Test
    void getItemById_Success() {
        User user = createTestUser("Bob", "bob@mail.com");
        ItemDto itemDto = ItemDto.builder()
                .name("Item1")
                .description("Desc1")
                .available(true)
                .build();

        Item item = itemService.create(user.getId(), itemDto);

        Item found = itemService.getItemById(item.getId());

        assertThat(found)
                .isNotNull()
                .extracting(
                        Item::getName,
                        Item::getDescription,
                        Item::isAvailable,
                        Item::getOwnerId
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
                .hasMessageContaining("Item not found");
    }

    @Test
    void getItemsByOwner_Success() {
        User user = createTestUser("Bob", "bob@mail.com");

        createItem(user.getId(), "Item1", "Desc1", true);
        createItem(user.getId(), "Item2", "Desc2", false);

        var items = itemService.getItemsByOwner(user.getId());

        assertThat(items)
                .isNotEmpty()
                .hasSize(2)
                .extracting(Item::getName)
                .containsExactlyInAnyOrder("Item1", "Item2");
    }

    @Test
    void getItemsByOwner_EmptyList() {
        User user = createTestUser("Bob", "bob@mail.com");

        var items = itemService.getItemsByOwner(user.getId());

        assertThat(items)
                .isEmpty();
    }

    @Test
    void getItemsByOwner_FilterByOwner() {
        User user1 = createTestUser("Bob", "bob@mail.com");
        User user2 = createTestUser("Alex", "alex@mail.com");

        createItem(user1.getId(), "Item1", "Desc1", true);
        createItem(user2.getId(), "Item2", "Desc2", true);
        createItem(user1.getId(), "Item3", "Desc3", true);

        var items = itemService.getItemsByOwner(user1.getId());

        assertThat(items)
                .hasSize(2)
                .extracting(Item::getName)
                .containsExactlyInAnyOrder("Item1", "Item3");
    }

    @Test
    void getItemsByOwner_OwnerIdCorrect() {
        User user = createTestUser("Bob", "bob@mail.com");

        createItem(user.getId(), "Item1", "Desc1", true);

        var items = itemService.getItemsByOwner(user.getId());

        assertThat(items)
                .isNotEmpty()
                .extracting(Item::getOwnerId)
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
        User user = createTestUser("Bob", "bob@mail.com");

        createItem(user.getId(), "Drill", "Power tool", true);
        createItem(user.getId(), "Hammer", "Steel tool", true);

        var result = itemService.searchByText("drill");

        assertThat(result)
                .isNotEmpty()
                .extracting(Item::getName)
                .contains("Drill");
    }

    @Test
    void searchByText_CaseInsensitive() {
        User user = createTestUser("Bob", "bob@mail.com");

        createItem(user.getId(), "DRILL", "Power tool", true);

        var result = itemService.searchByText("drill");

        assertThat(result)
                .isNotEmpty()
                .extracting(Item::getName)
                .contains("DRILL");
    }



}
