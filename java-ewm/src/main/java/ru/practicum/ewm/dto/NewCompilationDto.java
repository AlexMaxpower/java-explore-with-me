package ru.practicum.ewm.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class NewCompilationDto {

    @NotBlank
    private String title;
    private boolean pinned = false;
    private List<Long> events;

}
