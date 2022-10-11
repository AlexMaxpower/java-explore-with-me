package ru.practicum.ewm.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.other.EventSort;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.service.CompilationService;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.StatsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;


@Slf4j
@RestController
public class PublicController {

    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;
    private final StatsService statsService;

    @Autowired
    public PublicController(CategoryService categoryService, EventService eventService,
                            CompilationService compilationService, StatsService statsService) {
        this.categoryService = categoryService;
        this.eventService = eventService;
        this.compilationService = compilationService;
        this.statsService = statsService;
    }

    @GetMapping("/categories/{categoryId}")
    public CategoryDto getCategoryById(@PathVariable Long categoryId, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на получение категории с ID={}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                categoryId);
        return categoryService.getCategoryById(categoryId);
    }

    @GetMapping("/categories")
    public Collection<CategoryDto> getCategories(@Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Valid @Positive @RequestParam(defaultValue = "10") Integer size,
                                                 HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на получение списка категорий",
                request.getRemoteAddr(),
                request.getRequestURI());
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на получение события с ID={}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                eventId);
        EventFullDto eventFullDto = eventService.getEventByIdForPublic(eventId);
        statsService.setHits(request.getRequestURI(), request.getRemoteAddr());
        return eventFullDto;
    }

    @GetMapping(path = "/events")
    public Collection<EventShortDto> getEvents(@RequestParam(required = false, defaultValue = "") String text,
                                               @RequestParam(required = false) Set<Long> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE,
                                                       pattern = "yyyy-MM-dd HH:mm:ss")
                                               @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               @RequestParam(required = false) LocalDateTime rangeStart,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE,
                                                       pattern = "yyyy-MM-dd HH:mm:ss")
                                               @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               @RequestParam(required = false) LocalDateTime rangeEnd,
                                               @RequestParam(required = false) Boolean onlyAvailable,
                                               @RequestParam(required = false) EventSort sort,
                                               @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Valid @Positive @RequestParam(defaultValue = "10") Integer size,
                                               HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на получение списка событий",
                request.getRemoteAddr(), request.getRequestURI());
        statsService.setHits(request.getRequestURI(), request.getRemoteAddr());
        return eventService.getEventsForPublic(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable,
                sort, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на получение подборки с ID={}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                compId);
        return compilationService.getCompilationById(compId);
    }

    @GetMapping("/compilations")
    public Collection<CompilationDto> getCompilations(@Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                      @Valid @Positive @RequestParam(defaultValue = "10") Integer size,
                                                      @Valid @RequestParam(required = false) Boolean pinned,
                                                      HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на получение подборок начиная с {} в количестве {}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                from,
                size);
        return compilationService.getCompilations(from, size, pinned);
    }
}