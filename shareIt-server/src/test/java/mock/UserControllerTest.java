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
import ru.yandex.practicum.exceptions.DuplicatedDataException;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.user.UserController;
import ru.yandex.practicum.user.UserDto;
import ru.yandex.practicum.user.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = ShareItApplication.class)
public class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                1L,
                "John",
                "john.doe@mail.com");
    }


    @Test
    void getAllUsers() throws Exception {

        when(userService.getAllUsers())
                .thenReturn(List.of(userDto, userDto, userDto));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).getAllUsers();

    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(1L))
                .thenReturn(userDto);

        mvc.perform(get("/users/{userId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).getUserById(1L);
    }


    @Test
    void getUserById_notFound() throws Exception {
        when(userService.getUserById(1000L)).thenThrow(new NotFoundException("User not found"));

        mvc.perform(get("/users/{userId}", 1000L))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(1000L);

    }


    @Test
    void createUser() throws Exception {
        when(userService.createUser(userDto)).thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).createUser(userDto);
    }

    @Test
    void createUser_invalidEmail() throws Exception {
        UserDto invalid = new UserDto(1L, "John", "not-an-email");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalid))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_EmailExist() throws Exception {
        when(userService.createUser(userDto))
                .thenThrow(new DuplicatedDataException("Пользователь с таким email уже существует"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }


    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(1L, userDto)).thenReturn(userDto);

        mvc.perform(patch("/users/{userId}", userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).updateUser(1L, userDto);
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }


}
