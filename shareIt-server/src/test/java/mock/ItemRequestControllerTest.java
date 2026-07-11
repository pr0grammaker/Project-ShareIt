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
import ru.yandex.practicum.request.ItemRequestController;
import ru.yandex.practicum.request.ItemRequestDto;
import ru.yandex.practicum.request.ItemRequestResponseDto;
import ru.yandex.practicum.request.ItemRequestService;
import ru.yandex.practicum.user.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@ContextConfiguration(classes = ShareItApplication.class)
public class ItemRequestControllerTest {

    @MockitoBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    private UserDto userDto;

    private ItemRequestDto itemRequestDto;

    private ItemRequestResponseDto requestResponseDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(5L)
                .name("Артём")
                .email("artem@example.com")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .description("Text description")
                .build();

        requestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void create() throws Exception {
        when(itemRequestService.create(userDto.getId(), itemRequestDto))
                .thenReturn(requestResponseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(requestResponseDto.getId()))
                .andExpect(jsonPath("$.description").value(requestResponseDto.getDescription()))
                .andExpect(jsonPath("$.created").value(requestResponseDto.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.items").value(requestResponseDto.getItems()));
    }

    @Test
    void getByUserId() throws Exception {
        when(itemRequestService.get(userDto.getId()))
                .thenReturn(List.of(requestResponseDto, requestResponseDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userDto.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getAll() throws Exception {
        when(itemRequestService.getAll())
                .thenReturn(List.of(requestResponseDto, requestResponseDto, requestResponseDto, requestResponseDto,
                        requestResponseDto, requestResponseDto, requestResponseDto, requestResponseDto));

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(8)));

    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getById(userDto.getId(), requestResponseDto.getId()))
                .thenReturn(requestResponseDto);

        mockMvc.perform(get("/requests/{requestId}", requestResponseDto.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestResponseDto.getId()))
                .andExpect(jsonPath("$.description").value(requestResponseDto.getDescription()))
                .andExpect(jsonPath("$.created").value(requestResponseDto.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.items").value(requestResponseDto.getItems()));
    }


}