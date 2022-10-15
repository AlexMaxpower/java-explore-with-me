package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.ewm.other.Status;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Long id;
    @Size(min = 3, max = 2000)
    private String text;
    private LocalDateTime created;
    @JsonProperty("event")
    private Long eventId;
    @JsonProperty("commentator")
    private Long commentatorId;
    private Status status;

}