package ru.practicum.stats.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EndpointHit {

    private Long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;

}