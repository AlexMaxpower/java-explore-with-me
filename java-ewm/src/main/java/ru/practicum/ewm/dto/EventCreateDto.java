package ru.practicum.ewm.dto;

import lombok.*;
import ru.practicum.ewm.other.Location;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class EventCreateDto {

    private String annotation;
    private CategoryDto categoryDto;
    private LocalDateTime createdOn;
    private String description;
    private LocalDateTime eventDate;
    private UserDto initiator;
    private Location location;
    private boolean paid;
    private Integer participantLimit;
    private boolean requestModeration;
    private String title;

}
