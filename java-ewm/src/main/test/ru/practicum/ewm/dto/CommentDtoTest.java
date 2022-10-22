package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.ewm.other.Status;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {

    private JacksonTester<CommentDto> json;
    private CommentDto commentDto;

    public CommentDtoTest(@Autowired JacksonTester<CommentDto> json) {
        this.json = json;
    }

    @BeforeEach
    void beforeEach() {
        commentDto = new CommentDto(
                1L,
                "New comment about event",
                LocalDateTime.of(2000,1,1,0,0),
                2L,
                3L,
                Status.PENDING
        );
    }

    @Test
    void testJsonCommentDto() throws Exception {

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("New comment about event");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2000-01-01T00:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.event").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.commentator").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("PENDING");
    }
}
