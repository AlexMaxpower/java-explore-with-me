package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.facade.UserEventFacade;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/users")
public class PrivateRequestController {

    private final UserEventFacade userEventFacade;

    @Autowired
    public PrivateRequestController(UserEventFacade userEventFacade) {
        this.userEventFacade = userEventFacade;
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto create(@Valid @RequestParam Long eventId,
                                          @PathVariable Long userId,
                                          HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на участие в событии с ID={} от пользователя с ID={}",
                request.getRemoteAddr(), request.getRequestURI(), eventId, userId);
        return userEventFacade.createRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable Long userId,
                                          @PathVariable Long requestId,
                                          HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на отмену заявки на участие с ID={} от пользователя с ID={}",
                request.getRemoteAddr(), request.getRequestURI(), requestId, userId);
        return userEventFacade.cancelRequest(userId, requestId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestsByUserId(@PathVariable Long userId,
                                                             HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на получение заявок на участие от пользователя с ID={}",
                request.getRemoteAddr(), request.getRequestURI(), userId);
        return userEventFacade.getRequestsByUserId(userId);
    }
}