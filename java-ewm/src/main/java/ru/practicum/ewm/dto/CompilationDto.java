package ru.practicum.ewm.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class CompilationDto {

    private Long id;
    private String title;
    private boolean pinned;
    private Set<EventShortDto> events;

}
