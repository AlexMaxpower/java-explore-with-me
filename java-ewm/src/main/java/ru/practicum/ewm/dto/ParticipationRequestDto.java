package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ewm.other.Status;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ParticipationRequestDto {

    private Long id;
    private LocalDateTime created;
    @JsonProperty("event")
    @NotNull
    private Long eventId;
    @JsonProperty("requester")
    @NotNull
    private Long requesterId;
    private Status status;

}
