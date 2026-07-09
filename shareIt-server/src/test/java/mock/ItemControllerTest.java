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
import ru.yandex.practicum.item.*;
import ru.yandex.practicum.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemController.class)
@ContextConfiguration(classes = ShareItApplication.class)
public class ItemControllerTest {

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    private ItemDto itemDto;

    private UserDto userDto;

    private CommentResponseDto commentResponseDto;

    private CommentDto commentDto;

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

        commentDto = CommentDto.builder()
                .text("Text")
                .build();

        commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text(commentDto.getText())
                .authorName("Author")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void createItem() throws Exception {
        when(itemService.create(1L, itemDto)).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.ownerId").value(itemDto.getOwnerId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
        verify(itemService, times(1)).create(1L, itemDto);
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.update(userDto.getId(), itemDto.getId(), itemDto))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.ownerId").value(itemDto.getOwnerId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

        verify(itemService, times(1)).update(userDto.getId(), itemDto.getId(), itemDto);

    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(itemDto.getId())).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.ownerId").value(itemDto.getOwnerId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
        ;
    }

    @Test
    void getAllItemsByOwner() throws Exception {
        when(itemService.getItemsByOwner(userDto.getId()))
                .thenReturn(List.of(itemDto, itemDto, itemDto, itemDto, itemDto, itemDto, itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userDto.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(7)));
    }

    @Test
    void searchItemsByText() throws Exception {
        when(itemService.searchByText("Text"))
                .thenReturn(List.of(itemDto, itemDto, itemDto, itemDto, itemDto, itemDto, itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Text")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(7)));
    }

    @Test
    void searchItemsByText_DefaultValue() throws Exception {
        when(itemService.searchByText(""))
                .thenReturn(List.of(itemDto, itemDto, itemDto, itemDto, itemDto, itemDto, itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(7)));
    }

    @Test
    void createComment() throws Exception {
        when(commentService.createComment(userDto.getId(), itemDto.getId(), commentDto))
                .thenReturn(commentResponseDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentResponseDto.getId()))
                .andExpect(jsonPath("$.text").value(commentResponseDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentResponseDto.getAuthorName()))
                .andExpect(jsonPath("$.created")
                        .value(commentResponseDto.getCreated()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))));

    }

    @Test
    void getCommentsByItemId() throws Exception {

        when(itemService.getCommentsByItemId(userDto.getId(), itemDto.getId()))
                .thenReturn(List.of(itemDto, itemDto, itemDto, itemDto, itemDto));

        mockMvc.perform(get("/items/{itemId}/comments", itemDto.getId())
                        .header("X-Sharer-User-Id", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }


}
