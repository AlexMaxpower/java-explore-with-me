package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.other.EventSort;
import ru.practicum.ewm.other.Status;
import ru.practicum.ewm.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Set;


@Slf4j
@RestController
public class PublicController {

    @Value("${format.pattern.datetime}")
    private String dateTimeFormat;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;
    private final StatsService statsService;

    private final CommentService commentService;

    @Autowired
    public PublicController(CategoryService categoryService, EventService eventService,
                            CompilationService compilationService, StatsService statsService,
                            CommentService commentService) {
        this.categoryService = categoryService;
        this.eventService = eventService;
        this.compilationService = compilationService;
        this.statsService = statsService;
        this.commentService = commentService;
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
                                               @RequestParam(required = false) String rangeStart,
                                               @RequestParam(required = false) String rangeEnd,
                                               @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam(required = false) EventSort sort,
                                               @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Valid @Positive @RequestParam(defaultValue = "10") Integer size,
                                               HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на получение списка событий",
                request.getRemoteAddr(), request.getRequestURI());
        statsService.setHits(request.getRequestURI(), request.getRemoteAddr());

        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart,
                DateTimeFormatter.ofPattern(dateTimeFormat)) : LocalDateTime.now();

        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd,
                DateTimeFormatter.ofPattern(dateTimeFormat)) : LocalDateTime.now().plusYears(300);

        return eventService.getEventsForPublic(text, categories, paid, start, end,
                onlyAvailable, sort, from, size);
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

    @GetMapping(path = "/comments")
    public Collection<CommentDto> getComments(@RequestParam(required = false, defaultValue = "") String text,
                                              @RequestParam(required = false) Set<Long> events,
                                              @RequestParam(required = false) String rangeStart,
                                              @RequestParam(required = false) String rangeEnd,
                                              @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Valid @Positive @RequestParam(defaultValue = "10") Integer size,
                                              HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на получение списка комментариев",
                request.getRemoteAddr(), request.getRequestURI());

        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart,
                DateTimeFormatter.ofPattern(dateTimeFormat)) : null;

        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd,
                DateTimeFormatter.ofPattern(dateTimeFormat)) : null;

        return commentService.getComments(text, events, Status.CONFIRMED, start, end, from, size);
    }
}