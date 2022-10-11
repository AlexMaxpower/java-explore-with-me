package ru.practicum.stats.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.service.StatsService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
public class StatsController {

    private final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    public EndpointHit hit(@RequestBody EndpointHit endpointHit, HttpServletRequest request) {
        log.info("Запрос к эндпоинту '{}' на добавление статистики {}",
                request.getRequestURI(), endpointHit);
        return statsService.create(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> stats(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            @RequestParam
            LocalDateTime start,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            @RequestParam LocalDateTime end,
            @RequestParam List<String> uris,
            @RequestParam(defaultValue = "false")
            boolean unique,
            HttpServletRequest request) {

        log.info("Запрос к эндпоинту '{}' на получение статистики", request.getRequestURI());
        return statsService.getStats(start, end, uris, unique);

    }
}