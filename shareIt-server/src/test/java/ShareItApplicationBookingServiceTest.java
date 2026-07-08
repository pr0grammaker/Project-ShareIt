import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.ShareItApplication;
import ru.yandex.practicum.booking.BookingDto;
import ru.yandex.practicum.booking.BookingRepository;
import ru.yandex.practicum.booking.BookingResponseDto;
import ru.yandex.practicum.booking.BookingService;
import ru.yandex.practicum.enums.BookingState;
import ru.yandex.practicum.enums.Status;
import ru.yandex.practicum.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.item.ItemDto;
import ru.yandex.practicum.item.ItemRepository;
import ru.yandex.practicum.item.ItemService;
import ru.yandex.practicum.user.UserDto;
import ru.yandex.practicum.user.UserRepository;
import ru.yandex.practicum.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = ShareItApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ShareItApplicationBookingServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepositoryDb;

    @Autowired
    private ItemRepository itemRepositoryDb;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void clearDb() {
        bookingRepository.deleteAll();
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

    private BookingResponseDto createBooking(
            long userId, LocalDateTime bookingStart, LocalDateTime bookingEnd, long itemId) {

        BookingDto bookingDto = BookingDto.builder()
                .bookingStart(bookingStart)
                .bookingEnd(bookingEnd)
                .itemId(itemId)
                .build();

        return bookingService.createBooking(userId, bookingDto);
    }

    @Test
    void createBooking_Success() {
        UserDto user = createTestUser("Antuan", "bob1qD1@mail.com");
        ItemDto item = createItem(
                user.getId(),
                "Item1",
                "Desc1",
                true
        );

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        BookingResponseDto booking = createBooking(user.getId(), start, end, item.getId());

        assertThat(booking)
                .isNotNull()
                .extracting(
                        BookingResponseDto::getBookingStart,
                        BookingResponseDto::getBookingEnd,
                        BookingResponseDto::getBooker,
                        BookingResponseDto::getStatus
                )
                .containsExactly(
                        start,
                        end,
                        user,
                        Status.WAITING
                );
    }

    @Test
    void getBookings_SuccessByBooker() {
        UserDto user1 = createTestUser("Piter", "bob1qD1@mail.com");
        UserDto user2 = createTestUser("John", "always1qD1@mail.com");

        ItemDto item1 = createItem(
                user1.getId(),
                "Item1",
                "Desc2",
                true
        );

        ItemDto item2 = createItem(
                user1.getId(),
                "Item2",
                "Desc1",
                true
        );

        ItemDto item3 = createItem(
                user1.getId(),
                "Item3",
                "Desc3",
                true
        );

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        createBooking(user2.getId(), start, end, item1.getId());
        createBooking(user2.getId(), start, end, item2.getId());
        createBooking(user2.getId(), start, end, item3.getId());

        List<BookingResponseDto> bookings =
                bookingService.getBookingsByBooker(user2.getId(), BookingState.ALL);

        assertThat(bookings)
                .isNotNull()
                .hasSize(3)
                .extracting(BookingResponseDto::getStatus)
                .containsOnly(Status.WAITING);
    }

    @Test
    void getBookings_SuccessByOwner() {
        UserDto owner = createTestUser("Piter", "piter@mail.com");
        UserDto booker = createTestUser("John", "john@mail.com");

        ItemDto item1 = createItem(owner.getId(), "Item1", "Desc1", true);
        ItemDto item2 = createItem(owner.getId(), "Item2", "Desc2", true);
        ItemDto item3 = createItem(owner.getId(), "Item3", "Desc3", true);

        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = start.plusDays(1);

        createBooking(booker.getId(), start, end, item1.getId());
        createBooking(booker.getId(), start, end, item2.getId());
        createBooking(booker.getId(), start, end, item3.getId());

        List<BookingResponseDto> bookings =
                bookingService.getBookingsByOwner(owner.getId(), BookingState.ALL);

        assertThat(bookings)
                .isNotNull()
                .hasSize(3)
                .allSatisfy(b -> {
                    assertThat(b.getBookingStart()).isEqualToIgnoringNanos(start);
                    assertThat(b.getBookingEnd()).isEqualToIgnoringNanos(end);
                    assertThat(b.getBooker().getId()).isEqualTo(booker.getId());
                    assertThat(b.getStatus()).isEqualTo(Status.WAITING);
                });
    }


    @Test
    void getBookings_ThrowNotFoundExceptionByBooker() {
        assertThatThrownBy(() ->
                bookingService.getBookingsByBooker(999, BookingState.ALL)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void getBookings_ThrowNotFoundExceptionByOwner() {
        assertThatThrownBy(() ->
                bookingService.getBookingsByOwner(999, BookingState.ALL)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void createBooking_ThrowNotFoundExceptionUser() {
        UserDto user = createTestUser("Antuan", "bob1qD1@mail.com");
        ItemDto item = createItem(
                user.getId(),
                "Item1",
                "Desc1",
                true
        );

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        assertThatThrownBy(() ->
                createBooking(999, start, end, item.getId())
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void createBooking_ThrowNotFoundExceptionItem() {
        UserDto user = createTestUser("Antuan", "bob1qD1@mail.com");

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        assertThatThrownBy(() ->
                createBooking(user.getId(), start, end, 999)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь не найдена");
    }

    @Test
    void createBooking_ThrowConditionsNotMetExceptionStartDate() {
        UserDto user = createTestUser("Antuan", "bob1qD1@mail.com");
        ItemDto item = createItem(
                user.getId(),
                "Item1",
                "Desc1",
                true
        );

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        assertThatThrownBy(() ->
                createBooking(user.getId(), null, end, item.getId())
        )
                .isInstanceOf(ConditionsNotMetException.class)
                .hasMessageContaining("Дата начала или окончания бронирования не может быть пустой");
    }

    @Test
    void createBooking_ThrowConditionsNotMetExceptionEndDate() {
        UserDto user = createTestUser("Antuan", "bob1qD1@mail.com");
        ItemDto item = createItem(
                user.getId(),
                "Item1",
                "Desc1",
                true
        );

        LocalDateTime start = LocalDateTime.now();

        assertThatThrownBy(() ->
                createBooking(user.getId(), start, null, item.getId())
        )
                .isInstanceOf(ConditionsNotMetException.class)
                .hasMessageContaining("Дата начала или окончания бронирования не может быть пустой");
    }

    @Test
    void createBooking_ThrowConditionsNotMetExceptionStartEqualEnd() {
        UserDto user = createTestUser("Antuan", "bob1qD1@mail.com");
        ItemDto item = createItem(
                user.getId(),
                "Item1",
                "Desc1",
                true
        );

        LocalDateTime start = LocalDateTime.now();

        assertThatThrownBy(() ->
                createBooking(user.getId(), start, start, item.getId())
        )
                .isInstanceOf(ConditionsNotMetException.class)
                .hasMessageContaining("Дата начала и окончания не могут совпадать");
    }

    @Test
    void createBooking_ThrowConditionsNotMetExceptionEndIsBeforeStart() {
        UserDto user = createTestUser("Antuan", "bob1qD1@mail.com");
        ItemDto item = createItem(
                user.getId(),
                "Item1",
                "Desc1",
                true
        );

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        assertThatThrownBy(() ->
                createBooking(user.getId(), end, start, item.getId())
        )
                .isInstanceOf(ConditionsNotMetException.class)
                .hasMessageContaining("Дата окончания должна быть позже даты начала");
    }

    @Test
    void createBooking_ThrowConditionsNotMetExceptionItemNotAvailable() {
        UserDto user = createTestUser("Antuan", "bob1qD1@mail.com");
        ItemDto item = createItem(
                user.getId(),
                "Item1",
                "Desc1",
                false
        );

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        assertThatThrownBy(() ->
                createBooking(user.getId(), start, end, item.getId())
        )
                .isInstanceOf(ConditionsNotMetException.class)
                .hasMessageContaining("Вещь не доступна");
    }


    @Test
    void getBookingById_SuccessForBooker() {
        UserDto owner = createTestUser("Owner", "owner@mail.com");
        UserDto booker = createTestUser("Booker", "booker@mail.com");
        ItemDto item = createItem(owner.getId(), "Item1", "Desc1", true);

        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = start.plusDays(1);

        BookingResponseDto booking = createBooking(booker.getId(), start, end, item.getId());

        BookingResponseDto found = bookingService.getBookingById(booking.getId(), booker.getId());

        assertThat(found)
                .isNotNull()
                .extracting(
                        b -> b.getBooker().getId(),
                        b -> b.getItem().getId()
                )
                .containsExactly(booker.getId(), item.getId());


    }

    @Test
    void getBookingById_ThrowNotFoundExceptionUserNotFound() {
        UserDto owner = createTestUser("Owner", "owner@mail.com");
        ItemDto item = createItem(owner.getId(), "Item1", "Desc1", true);

        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = start.plusDays(1);

        BookingResponseDto booking = createBooking(owner.getId(), start, end, item.getId());

        assertThatThrownBy(() ->
                bookingService.getBookingById(booking.getId(), 999L)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void getBookingById_ThrowNotFoundExceptionBookingNotFound() {
        UserDto user = createTestUser("User", "user@mail.com");

        assertThatThrownBy(() ->
                bookingService.getBookingById(999L, user.getId())
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Бронирование отсутствует");
    }

    @Test
    void getBookingById_ThrowNotFoundExceptionAccessDenied() {
        UserDto owner = createTestUser("Owner", "owner@mail.com");
        UserDto booker = createTestUser("Booker", "booker@mail.com");
        UserDto stranger = createTestUser("Stranger", "stranger@mail.com");

        ItemDto item = createItem(owner.getId(), "Item1", "Desc1", true);

        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = start.plusDays(1);

        BookingResponseDto booking = createBooking(booker.getId(), start, end, item.getId());

        assertThatThrownBy(() ->
                bookingService.getBookingById(booking.getId(), stranger.getId())
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не имеет доступа к бронированию");
    }

    @Test
    void updateApprovalStatus_SuccessApproved() {
        UserDto owner = createTestUser("Owner", "owner@mail.com");
        UserDto booker = createTestUser("Booker", "booker@mail.com");
        ItemDto item = createItem(owner.getId(), "Item1", "Desc1", true);

        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = start.plusDays(1);

        BookingResponseDto booking = createBooking(booker.getId(), start, end, item.getId());

        BookingResponseDto updated =
                bookingService.updateApprovalStatus(booking.getId(), owner.getId(), true);

        assertThat(updated)
                .isNotNull()
                .satisfies(b -> {
                    assertThat(b.getStatus()).isEqualTo(Status.APPROVED);
                    assertThat(b.getBooker().getId()).isEqualTo(booker.getId());
                    assertThat(b.getItem().getId()).isEqualTo(item.getId());
                });
    }

    @Test
    void updateApprovalStatus_SuccessRejected() {
        UserDto owner = createTestUser("Owner", "owner@mail.com");
        UserDto booker = createTestUser("Booker", "booker@mail.com");
        ItemDto item = createItem(owner.getId(), "Item1", "Desc1", true);

        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = start.plusDays(1);

        BookingResponseDto booking = createBooking(booker.getId(), start, end, item.getId());

        BookingResponseDto updated =
                bookingService.updateApprovalStatus(booking.getId(), owner.getId(), false);

        assertThat(updated.getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void updateApprovalStatus_ThrowNotFoundExceptionBookingNotFound() {
        UserDto owner = createTestUser("Owner", "owner@mail.com");

        assertThatThrownBy(() ->
                bookingService.updateApprovalStatus(999L, owner.getId(), true)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Бронирование отсутствует");
    }

    @Test
    void updateApprovalStatus_ThrowConditionsNotMetExceptionNotOwner() {
        UserDto owner = createTestUser("Owner", "owner@mail.com");
        UserDto booker = createTestUser("Booker", "booker@mail.com");
        UserDto stranger = createTestUser("Stranger", "stranger@mail.com");

        ItemDto item = createItem(owner.getId(), "Item1", "Desc1", true);

        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = start.plusDays(1);

        BookingResponseDto booking = createBooking(booker.getId(), start, end, item.getId());

        assertThatThrownBy(() ->
                bookingService.updateApprovalStatus(booking.getId(), stranger.getId(), true)
        )
                .isInstanceOf(ConditionsNotMetException.class)
                .hasMessageContaining("Пользователь не является владельцем вещи");
    }



}
