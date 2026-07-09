package mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.ShareItApplication;
import ru.yandex.practicum.booking.BookingController;
import ru.yandex.practicum.booking.BookingDto;
import ru.yandex.practicum.booking.BookingResponseDto;
import ru.yandex.practicum.booking.BookingService;
import ru.yandex.practicum.enums.BookingState;
import ru.yandex.practicum.enums.Status;
import ru.yandex.practicum.item.ItemDto;
import ru.yandex.practicum.user.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@ContextConfiguration(classes = ShareItApplication.class)
public class BookingControllerTest {

    @MockitoBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    private ItemDto itemDto;

    private UserDto userDto;

    private BookingResponseDto bookingResponseDto;

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(5L)
                .name("Артём")
                .email("artem@example.com")
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .ownerId(23L)
                .name("Название")
                .description("Описание")
                .available(true)
                .requestId(2L)
                .comments(new ArrayList<>())
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .bookingStart(LocalDateTime.now())
                .bookingEnd(LocalDateTime.now().plusDays(4))
                .itemId(itemDto.getId())
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .bookingStart(LocalDateTime.now())
                .bookingEnd(LocalDateTime.now().plusDays(3))
                .item(itemDto)
                .booker(userDto)
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(userDto.getId(), bookingDto))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$.bookingStart").value(bookingResponseDto.getBookingStart()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.bookingEnd").value(bookingResponseDto.getBookingEnd()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.item.id").value(bookingResponseDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingResponseDto.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingResponseDto.getItem().getDescription()))
                .andExpect(jsonPath("$.booker.id").value(bookingResponseDto.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingResponseDto.getBooker().getName()))
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus().toString()));
    }

    @Test
    void updateApprovalStatus() throws Exception {
        when(bookingService.updateApprovalStatus(bookingDto.getId(), 23L, true))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 23L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)

                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$.bookingStart").value(bookingResponseDto.getBookingStart()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.bookingEnd").value(bookingResponseDto.getBookingEnd()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.item.id").value(bookingResponseDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingResponseDto.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingResponseDto.getItem().getDescription()))
                .andExpect(jsonPath("$.booker.id").value(bookingResponseDto.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingResponseDto.getBooker().getName()))
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus().toString()));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(userDto.getId(), bookingDto.getId()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$.bookingStart").value(bookingResponseDto.getBookingStart()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.bookingEnd").value(bookingResponseDto.getBookingEnd()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.item.id").value(bookingResponseDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingResponseDto.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingResponseDto.getItem().getDescription()))
                .andExpect(jsonPath("$.booker.id").value(bookingResponseDto.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingResponseDto.getBooker().getName()))
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus().toString()));
    }

    @Test
    void getBookingsByBooker() throws Exception {
        when(bookingService.getBookingsByBooker(userDto.getId(), BookingState.ALL))
                .thenReturn(List.of(bookingResponseDto, bookingResponseDto, bookingResponseDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void getBookingsByOwner() throws Exception {
        when(bookingService.getBookingsByOwner(userDto.getId(), BookingState.ALL))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}