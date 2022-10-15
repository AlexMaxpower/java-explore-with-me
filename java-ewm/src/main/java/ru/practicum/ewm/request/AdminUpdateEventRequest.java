package ru.practicum.ewm.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import ru.practicum.ewm.other.Location;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AdminUpdateEventRequest {

    private String annotation;
    @JsonProperty("category")
    private Long categoryId;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;

}