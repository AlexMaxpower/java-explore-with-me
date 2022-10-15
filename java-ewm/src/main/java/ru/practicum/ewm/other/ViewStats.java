package ru.practicum.ewm.other;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ViewStats {

    private String app;
    private String uri;
    @JsonProperty("hits")
    private Long views;

}