package ru.practicum.ewm.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import ru.practicum.ewm.other.Location;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AdminUpdateEventRequest {

    private String annotation;
    private Long categoryId;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;

}