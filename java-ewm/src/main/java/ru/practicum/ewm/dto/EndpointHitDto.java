package ru.practicum.ewm.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EndpointHitDto {

    private Long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;

}
