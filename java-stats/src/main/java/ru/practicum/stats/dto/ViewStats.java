package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewStats {

    private String app;
    private String uri;
    @JsonProperty("hits")
    private Long views;

}
