package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.facade.UserEventFacade;
import ru.practicum.ewm.request.UpdateEventRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class PrivateEventController {

    private final UserEventFacade userEventFacade;

    @Autowired
    public PrivateEventController(UserEventFacade userEventFacade) {
        this.userEventFacade = userEventFacade;
    }

    @PostMapping("/{userId}/events")
    public EventFullDto create(@Valid @RequestBody NewEventDto newEventDto,
                               @PathVariable Long userId,
                               HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на добавление события {}",
                request.getRemoteAddr(), request.getRequestURI(), newEventDto.toString());
        return userEventFacade.createEvent(newEventDto, userId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto cancel(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на отмену события с ID={}",
                request.getRemoteAddr(), request.getRequestURI(), eventId);
        return userEventFacade.cancelEvent(eventId, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUserEventById(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' пользователем с ID={} на получения события с ID={}",
                request.getRemoteAddr(), request.getRequestURI(), userId, eventId);
        return userEventFacade.getUserEventById(eventId, userId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirm(@PathVariable Long userId,
                                           @PathVariable Long eventId,
                                           @PathVariable Long reqId,
                                           HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на подтверждение заявки с ID={} " +
                        "на участие в событии с ID={} ",
                request.getRemoteAddr(), request.getRequestURI(), reqId, eventId);
        return userEventFacade.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto reject(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @PathVariable Long reqId,
                                          HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на отклонение заявки с ID={} " +
                        "на участие в событии с ID={} ",
                request.getRemoteAddr(), request.getRequestURI(), reqId, eventId);
        return userEventFacade.rejectRequest(userId, eventId, reqId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId,
                                                     @PathVariable Long eventId,
                                                     HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' от пользователя с ID={} " +
                        "на получение заявок на участие в событии с ID={} ",
                request.getRemoteAddr(), request.getRequestURI(), userId, eventId);
        return userEventFacade.getRequestsByEventId(userId, eventId);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvents(@PathVariable Long userId,
                                         @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Valid @Positive @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' от пользователя с ID={} " +
                        "на получение {} добавленных событий, начиная с {}",
                request.getRemoteAddr(), request.getRequestURI(), userId, size, from);
        return userEventFacade.getEventsByUserId(userId, from, size);
    }

    @PatchMapping("/{userId}/events")
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventRequest updateEventRequest,
                                    @PathVariable Long userId,
                                    HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' от пользователя с ID={} " +
                        "на обновление события {}",
                request.getRemoteAddr(), request.getRequestURI(), userId, updateEventRequest);
        return userEventFacade.updateEvent(userId, updateEventRequest);
    }
}
