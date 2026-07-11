package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.ShareItApplication;
import ru.yandex.practicum.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.item.ItemDto;
import ru.yandex.practicum.item.ItemRepository;
import ru.yandex.practicum.item.ItemService;
import ru.yandex.practicum.request.ItemRequestDto;
import ru.yandex.practicum.request.ItemRequestRepository;
import ru.yandex.practicum.request.ItemRequestResponseDto;
import ru.yandex.practicum.request.ItemRequestService;
import ru.yandex.practicum.user.UserDto;
import ru.yandex.practicum.user.UserRepository;
import ru.yandex.practicum.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = ShareItApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ShareItApplicationRequestServiceTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRequestService itemRequestService;

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
        itemRequestRepository.deleteAll();
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

    private ItemRequestResponseDto createRequest(long userId, String text) {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description(text)
                .build();

        return itemRequestService.create(userId, itemRequestDto);
    }

    @Test
    void createItemRequest_Success() {
        UserDto user = createTestUser("Manuel", "qdwgdiwhd@gmail.com");

        ItemRequestResponseDto request = createRequest(user.getId(), "I need fucking table");

        assertThat(request)
                .isNotNull()
                .satisfies(r -> {
                    assertThat(request.getId()).isNotNull().isPositive();
                    assertThat(r.getDescription()).isEqualTo("I need fucking table");
                    assertThat(r.getCreated()).isNotNull();
                    assertThat(r.getItems()).isEmpty();
                });

    }

    @Test
    void createItemRequest_ThrowNotFoundExceptionUser() {
        assertThatThrownBy(() ->
                createRequest(999, "I need fucking table")
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");

    }

    @Test
    void createItemRequest_ThrowConditionsNotMetExceptionItemAlreadyExist() {
        UserDto user = createTestUser("Manuel", "qdwgdiwhd@gmail.com");
        createItem(user.getId(), "Table", "Super puper table", true);

        assertThatThrownBy(() ->
                createRequest(user.getId(), "Super puper table")
        )
                .isInstanceOf(ConditionsNotMetException.class)
                .hasMessageContaining("Эта вещь уже существует");

    }

    @Test
    void getRequestsByUserId_Success() {
        UserDto user = createTestUser("Manuel", "qdwgdiwhd@gmail.com");
        createRequest(user.getId(), "I need fucking table");
        createRequest(user.getId(), "I need fucking window");
        createRequest(user.getId(), "I need fucking PC");

        Collection<ItemRequestResponseDto> requests = itemRequestService.get(user.getId());
        assertThat(requests)
                .isNotNull()
                .hasSize(3)
                .allSatisfy(r -> {
                    assertThat(r.getId()).isPositive();
                    assertThat(r.getDescription()).isNotBlank();
                    assertThat(r.getCreated()).isNotNull();
                    assertThat(r.getItems()).isEmpty();
                });

        assertThat(requests)
                .extracting(ItemRequestResponseDto::getDescription)
                .containsExactlyInAnyOrder(
                        "I need fucking table",
                        "I need fucking window",
                        "I need fucking PC"
                );
    }

    @Test
    void getRequestsByUserId_ThrowNotFoundExceptionUser() {
        UserDto user = createTestUser("Manuel", "qdwgdiwhd@gmail.com");
        createRequest(user.getId(), "I need table");
        createRequest(user.getId(), "I need window");
        createRequest(user.getId(), "I need PC");

        assertThatThrownBy(() -> itemRequestService.get(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void getAll_Success() {
        UserDto user1 = createTestUser("Manuel", "manuel1@mail.com");
        UserDto user2 = createTestUser("Manuel", "manuel2@mail.com");
        UserDto user3 = createTestUser("Manuel", "manuel3@mail.com");


        createRequest(user1.getId(), "Need table");
        createRequest(user2.getId(), "Need window");
        createRequest(user3.getId(), "Need PC");

        Collection<ItemRequestResponseDto> requests = itemRequestService.getAll();

        assertThat(requests)
                .isNotNull()
                .hasSize(3);

        List<ItemRequestResponseDto> list = new ArrayList<>(requests);
        assertThat(list)
                .extracting(ItemRequestResponseDto::getDescription)
                .containsExactly("Need PC", "Need window", "Need table");
    }

    @Test
    void getAll_NoRequests() {
        Collection<ItemRequestResponseDto> requests = itemRequestService.getAll();

        assertThat(requests)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void getById_Success() {
        UserDto user = createTestUser("Manuel", "manuel@mail.com");
        ItemRequestResponseDto created = createRequest(user.getId(), "Need table");

        ItemRequestResponseDto result = itemRequestService.getById(user.getId(), created.getId());

        assertThat(result)
                .isNotNull()
                .satisfies(r -> {
                    assertThat(r.getId()).isEqualTo(created.getId());
                    assertThat(r.getDescription()).isEqualTo("Need table");
                    assertThat(r.getCreated()).isNotNull();
                    assertThat(r.getItems()).isEmpty();
                });
    }

    @Test
    void getById_ThrowNotFoundExceptionUser() {
        UserDto user = createTestUser("Manuel", "manuel@mail.com");
        ItemRequestResponseDto created = createRequest(user.getId(), "Need window");

        assertThatThrownBy(() -> itemRequestService.getById(999L, created.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void getById_ThrowNotFoundExceptionRequest() {
        UserDto user = createTestUser("Manuel", "manuel@mail.com");

        assertThatThrownBy(() -> itemRequestService.getById(user.getId(), 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Запрос на вещь не найден");
    }


}
