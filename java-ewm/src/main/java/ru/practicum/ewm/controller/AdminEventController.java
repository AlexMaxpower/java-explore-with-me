package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.facade.UserEventFacade;
import ru.practicum.ewm.other.State;
import ru.practicum.ewm.request.AdminUpdateEventRequest;
import ru.practicum.ewm.service.EventService;

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
@RequestMapping(path = "/admin/events")
public class AdminEventController {

    @Value("${format.pattern.datetime}")
    private String dateTimeFormat;
    private final EventService eventService;
    private final UserEventFacade userEventFacade;

    @Autowired
    public AdminEventController(EventService eventService, UserEventFacade userEventFacade) {

        this.eventService = eventService;
        this.userEventFacade = userEventFacade;
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publish(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на публикацию события с ID={}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                eventId);
        return eventService.publish(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto reject(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на отклонение события с ID={}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                eventId);
        return eventService.reject(eventId);
    }

    @PutMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long eventId,
                               @RequestBody AdminUpdateEventRequest adminUpdateEventRequest,
                               HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на редактирование события с ID={}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                eventId);
        return userEventFacade.updateEventByAdmin(eventId, adminUpdateEventRequest);
    }

    @GetMapping
    public Collection<EventFullDto> getEvents(@RequestParam(required = false) Set<Long> users,
                                              @RequestParam(required = false) Set<State> states,
                                              @RequestParam(required = false) Set<Long> categories,
                                              @RequestParam(required = false) String rangeStart,
                                              @RequestParam(required = false) String rangeEnd,
                                              @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Valid @Positive @RequestParam(defaultValue = "10") Integer size,
                                              HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на получение списка событий",
                request.getRemoteAddr(), request.getRequestURI());

        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart,
                DateTimeFormatter.ofPattern(dateTimeFormat)) : LocalDateTime.now();

        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd,
                DateTimeFormatter.ofPattern(dateTimeFormat)) : LocalDateTime.now().plusYears(300);

        return eventService.getEventsForAdmin(users, states, categories, start, end, from, size);
    }
}
