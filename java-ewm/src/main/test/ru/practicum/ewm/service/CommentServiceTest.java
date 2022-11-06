package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.NotValidException;
import ru.practicum.ewm.other.Status;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"classpath:/resources/sql/1_clear_schema.sql",
        "classpath:/resources/sql/2_insert_user_data.sql",
        "classpath:/resources/sql/3_insert_category_data.sql",
        "classpath:/resources/sql/4_insert_event_data.sql"
})
public class CommentServiceTest {
    private final CommentService commentService;

    private CommentDto commentDto = new CommentDto(null, "First comment",
            LocalDateTime.of(2022, 10, 21, 0, 0, 0), 3001L,
            null, Status.PENDING);

    @Test
    void shouldCreateComment() {
        CommentDto resultCommentDto = commentService.createComment(1001L, commentDto);
        assertThat(resultCommentDto.getText(), equalTo(commentDto.getText()));
        assertThat(resultCommentDto.getCommentatorId(), equalTo(1001L));
        assertThat(resultCommentDto.getCreated(), equalTo(commentDto.getCreated()));
        assertThat(resultCommentDto.getEventId(), equalTo(commentDto.getEventId()));
        assertThat(resultCommentDto.getStatus(), equalTo(Status.PENDING));
    }

    @Test
    void shouldReturnCommentsByUserId() {
        CommentDto commentDto1 = commentService.createComment(1001L, commentDto);
        CommentDto commentDto2 = commentService.createComment(1002L, commentDto);
        List<CommentDto> resultListCommentDto = commentService.getCommentsByUserId(1001L);
        assertThat(resultListCommentDto.size(), equalTo(1));
        assertThat(resultListCommentDto.get(0).getText(), equalTo(commentDto1.getText()));
        assertThat(resultListCommentDto.get(0).getCommentatorId(), equalTo(commentDto1.getCommentatorId()));
        assertThat(resultListCommentDto.get(0).getCreated(), equalTo(commentDto1.getCreated()));
        assertThat(resultListCommentDto.get(0).getEventId(), equalTo(commentDto1.getEventId()));
        assertThat(resultListCommentDto.get(0).getStatus(), equalTo(Status.PENDING));
    }

    @Test
    void shouldConfirmedCommentByAdmin() {
        CommentDto commentDto1 = commentService.createComment(1001L, commentDto);
        CommentDto updateCommentDto = new CommentDto(null, null, null, null,
                null, Status.CONFIRMED);
        CommentDto resultCommentDto = commentService.updateCommentByAdmin(commentDto1.getId(), updateCommentDto);

        assertThat(resultCommentDto.getText(), equalTo(commentDto1.getText()));
        assertThat(resultCommentDto.getCommentatorId(), equalTo(commentDto1.getCommentatorId()));
        assertThat(resultCommentDto.getCreated(), equalTo(commentDto1.getCreated()));
        assertThat(resultCommentDto.getEventId(), equalTo(commentDto1.getEventId()));
        assertThat(resultCommentDto.getStatus(), equalTo(Status.CONFIRMED));
    }

    @Test
    void shouldRejectedCommentByAdmin() {
        CommentDto commentDto1 = commentService.createComment(1001L, commentDto);
        CommentDto updateCommentDto = new CommentDto(null, null, null, null,
                null, Status.REJECTED);
        CommentDto resultCommentDto = commentService.updateCommentByAdmin(commentDto1.getId(), updateCommentDto);

        assertThat(resultCommentDto.getText(), equalTo(commentDto1.getText()));
        assertThat(resultCommentDto.getCommentatorId(), equalTo(commentDto1.getCommentatorId()));
        assertThat(resultCommentDto.getCreated(), equalTo(commentDto1.getCreated()));
        assertThat(resultCommentDto.getEventId(), equalTo(commentDto1.getEventId()));
        assertThat(resultCommentDto.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void shouldExceptionWhenGetNotExistUser() {

        NotFoundException exp = assertThrows(NotFoundException.class,
                () -> commentService.getCommentById(-1L, 1001L));
        assertEquals("Comment with id=-1 not found.", exp.getMessage());
    }

    @Test
    void shouldReturnOnlyConfirmedComments() {
        CommentDto commentDto1 = commentService.createComment(1001L, commentDto);
        CommentDto commentDto2 = commentService.createComment(1002L, commentDto);
        commentDto1.setStatus(Status.CONFIRMED);
        commentService.updateCommentByAdmin(commentDto1.getId(), commentDto1);

        List<CommentDto> resultListCommentDto = commentService.getComments("", null, Status.CONFIRMED,
                null, null, 0, 10);

        assertThat(resultListCommentDto.size(), equalTo(1));
        assertThat(resultListCommentDto.get(0).getText(), equalTo(commentDto1.getText()));
        assertThat(resultListCommentDto.get(0).getCommentatorId(), equalTo(commentDto1.getCommentatorId()));
        assertThat(resultListCommentDto.get(0).getCreated(), equalTo(commentDto1.getCreated()));
        assertThat(resultListCommentDto.get(0).getEventId(), equalTo(commentDto1.getEventId()));
        assertThat(resultListCommentDto.get(0).getStatus(), equalTo(Status.CONFIRMED));
    }

    @Test
    void shouldReturnAllComments() {
        CommentDto commentDto1 = commentService.createComment(1001L, commentDto);
        CommentDto commentToUpdate = new CommentDto(null, null, null, null, null,
                Status.REJECTED);
        commentService.updateCommentByAdmin(commentDto1.getId(), commentToUpdate);
        CommentDto commentDto2 = commentService.createComment(1002L, commentDto);
        commentToUpdate.setStatus(Status.CONFIRMED);
        commentService.updateCommentByAdmin(commentDto2.getId(), commentToUpdate);

        List<CommentDto> resultListCommentDto = commentService.getComments("", null, null,
                null, null, 0, 10);

        assertThat(resultListCommentDto.size(), equalTo(2));
        assertThat(resultListCommentDto.get(0).getText(), equalTo(commentDto1.getText()));
        assertThat(resultListCommentDto.get(0).getCommentatorId(), equalTo(commentDto1.getCommentatorId()));
        assertThat(resultListCommentDto.get(0).getCreated(), equalTo(commentDto1.getCreated()));
        assertThat(resultListCommentDto.get(0).getEventId(), equalTo(commentDto1.getEventId()));
        assertThat(resultListCommentDto.get(0).getStatus(), equalTo(Status.REJECTED));

        assertThat(resultListCommentDto.get(1).getText(), equalTo(commentDto2.getText()));
        assertThat(resultListCommentDto.get(1).getCommentatorId(), equalTo(commentDto2.getCommentatorId()));
        assertThat(resultListCommentDto.get(1).getCreated(), equalTo(commentDto2.getCreated()));
        assertThat(resultListCommentDto.get(1).getEventId(), equalTo(commentDto2.getEventId()));
        assertThat(resultListCommentDto.get(1).getStatus(), equalTo(Status.CONFIRMED));
    }

    @Test
    void shouldExceptionWhenNoComment() {
        CommentDto commentToUpdateDto = commentService.createComment(1001L, commentDto);
        commentToUpdateDto.setText("1");
        NotValidException exp = assertThrows(NotValidException.class,
                () -> commentService.updateComment(1001L, commentToUpdateDto.getId(), commentToUpdateDto));
        assertEquals("Длина комментария должна быть больше 3 символов, но меньше 2000 символов",
                exp.getMessage());
    }
}
