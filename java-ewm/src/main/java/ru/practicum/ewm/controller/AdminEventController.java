package ru.practicum.ewm.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.util.Collection;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
public class AdminEventController {
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
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE,
                                                      pattern = "yyyy-MM-dd HH:mm:ss")
                                              @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                              @RequestParam(required = false) LocalDateTime rangeStart,
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE,
                                                      pattern = "yyyy-MM-dd HH:mm:ss")
                                              @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                              @RequestParam(required = false) LocalDateTime rangeEnd,
                                              @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Valid @Positive @RequestParam(defaultValue = "10") Integer size,
                                              HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на получение списка событий",
                request.getRemoteAddr(), request.getRequestURI());
        return eventService.getEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
