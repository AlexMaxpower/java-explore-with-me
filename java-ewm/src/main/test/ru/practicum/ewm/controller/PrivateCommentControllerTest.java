package ru.practicum.ewm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Comment;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.other.State;
import ru.practicum.ewm.other.Status;
import ru.practicum.ewm.service.CommentService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PrivateCommentController.class)
public class PrivateCommentControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    CommentService commentService;

    @Autowired
    private MockMvc mvc;

    private User user = new User(1L, "first@yandex.ru", "First user");

    private Category category = new Category(1L, "First category");

    private Event event = new Event(1L, "First event", "Annotation event", category, "Event description",
            LocalDateTime.of(2025, 1, 1, 0, 0), 37.4, 42.2,
            LocalDateTime.of(2022, 10, 20, 0, 0, 0), user, false, 50,
            null, true, State.PENDING);

    private Comment comment = new Comment(1L, "First comment about event ",
            LocalDateTime.of(2022, 10, 21, 0, 0, 0), event, user, Status.PENDING);

    private CommentDto commentDto = new CommentDto(1L, "First comment",
            LocalDateTime.of(2022, 10, 21, 0, 0, 0), 1L,
           1L, Status.PENDING);


    private DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Test
    void createComment() throws Exception {

        when(commentService.createComment(any(Long.class), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/users/1/comments")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.created",
                        is(commentDto.getCreated().format(formatter))))
                .andExpect(jsonPath("$.event", is(commentDto.getEventId()), Long.class))
                .andExpect(jsonPath("$.commentator", is(commentDto.getCommentatorId()), Long.class));
    }

    @Test
    void getComment() throws Exception {
        when(commentService.getCommentById(any(Long.class), any(Long.class)))
                .thenReturn(commentDto);
        mvc.perform(get("/users/1/comments/1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.created",
                        is(commentDto.getCreated().format(formatter))))
                .andExpect(jsonPath("$.event", is(commentDto.getEventId()), Long.class))
                .andExpect(jsonPath("$.commentator", is(commentDto.getCommentatorId()), Long.class));
    }
}