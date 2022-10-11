package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class CompilationDto {

    private Long id;
    private String title;
    private boolean pinned;
    private Set<EventShortDto> events;

}
