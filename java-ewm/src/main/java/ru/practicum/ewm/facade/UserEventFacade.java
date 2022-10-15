package ru.practicum.ewm.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotValidException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.other.State;
import ru.practicum.ewm.request.AdminUpdateEventRequest;
import ru.practicum.ewm.request.UpdateEventRequest;
import ru.practicum.ewm.service.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserEventFacade {

    private final UserService userService;
    private final EventService eventService;
    private final CategoryService categoryService;
    private UserMapper userMapper;
    private final EventMapper eventMapper;
    private CategoryMapper categoryMapper;
    private final CompilationService compilationService;
    private final CompilationMapper compilationMapper;
    private final RequestService requestService;

    @Autowired
    public UserEventFacade(UserService userService, EventService eventService, CategoryService categoryService,
                           UserMapper userMapper, EventMapper eventMapper, CategoryMapper categoryMapper,
                           CompilationService compilationService, CompilationMapper compilationMapper,
                           RequestService requestService) {
        this.userService = userService;
        this.eventService = eventService;
        this.categoryService = categoryService;
        this.userMapper = userMapper;
        this.eventMapper = eventMapper;
        this.categoryMapper = categoryMapper;
        this.compilationService = compilationService;
        this.compilationMapper = compilationMapper;
        this.requestService = requestService;
    }

    public EventFullDto createEvent(NewEventDto newEventDto, Long userId) {
        UserDto initiator = userService.getUserById(userId);
        CategoryDto categoryDto = categoryService.getCategoryById(newEventDto.getCategoryId());
        EventCreateDto eventCreateDto = eventMapper.newEventDtoToEventCreateDto(newEventDto,
                initiator, categoryDto);
        return eventService.create(eventCreateDto);
    }

    public EventFullDto cancelEvent(Long eventId, Long userId) {
        userService.getUserById(userId);
        return eventService.cancel(eventId, userId);
    }

    public EventFullDto getUserEventById(Long eventId, Long userId) {
        userService.getUserById(userId);
        return eventService.getEventByIdForUser(eventId, userId);
    }

    public void deleteEventFromComp(Long compId, Long eventId) {
        Event event = eventService.getEvent(eventId);
        compilationService.deleteEvent(compId, event);
    }

    public CompilationDto addEventToComp(Long compId, Long eventId) {
        Event event = eventService.getEvent(eventId);
        return compilationService.addEvent(compId, event);
    }

    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userService.getUser(userId);
        log.info("Получен пользователь {}", user);
        Event event = eventService.getEvent(eventId);
        log.info("Получено событие {}", event);
        if (event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException("Нельзя участвовать в неопубликованном событии!");
        }
        if (requestService.getRequestByUserIdAndEventId(userId, eventId) != null) {
            throw new ForbiddenException("Нельзя добавить повторный запрос!");
        }

        return requestService.createRequest(user, event);
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        User user = userService.getUser(userId);
        log.info("Получен пользователь {}", user);
        return requestService.cancelRequest(user, requestId);
    }

    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long requestId) {
        User user = userService.getUser(userId);
        log.info("Получен пользователь {}", user);
        Event event = eventService.getEvent(eventId);
        log.info("Получено событие {}", event);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Запрос на участие в событии может подтверждать только инициатор!");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException("Нельзя подтверждать запрос на участие в неопубликованном событии!");
        }

        return requestService.confirmRequest(event, requestId);
    }

    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long requestId) {
        User user = userService.getUser(userId);
        log.info("Получен пользователь {}", user);
        Event event = eventService.getEvent(eventId);
        log.info("Получено событие {}", event);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Запрос на участие в событии может отклонять только инициатор!");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException("Нельзя отклонять запрос на участие в неопубликованном событии!");
        }

        return requestService.rejectRequest(event, requestId);
    }

    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        User user = userService.getUser(userId);
        log.info("Получен пользователь {}", user);
        return requestService.getRequestsByUserId(userId);
    }

    public List<ParticipationRequestDto> getRequestsByEventId(Long userId, Long eventId) {
        User user = userService.getUser(userId);
        log.info("Получен пользователь {}", user);
        Event event = eventService.getEvent(eventId);
        log.info("Получено событие {}", user);

        return requestService.getRequestsByEventId(eventId);
    }

    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        User user = userService.getUser(userId);
        log.info("Получен пользователь {}", user);
        return eventService.getEventsByUserId(userId, from, size);
    }

    public EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest) {
        if (updateEventRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Дата и время события не могут быть раньше," +
                    " чем через два часа от текущего момента");
        }

        User user = userService.getUser(userId);
        log.info("Получен пользователь {}", user);

        Category category = null;
        if (updateEventRequest.getCategoryId() != null) {
            category = categoryService.getCategory(updateEventRequest.getCategoryId());
        }

        EventFullDto eventFullDto = eventService.updateEvent(userId, updateEventRequest, category);
        return eventFullDto;
    }

    public EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest) {

        Category category = null;
        if (adminUpdateEventRequest.getCategoryId() != null) {
            category = categoryService.getCategory(adminUpdateEventRequest.getCategoryId());
        }

        EventFullDto eventFullDto = eventService.updateEventByAdmin(eventId, adminUpdateEventRequest, category);
        return eventFullDto;
    }

    public void deleteCategory(Long categoryId) {

        List<EventShortDto> events = eventService.getEventsByCategoryId(categoryId);

        if (events.size() != 0) {
           throw new NotValidException("Нельзя удалить категорию с событиями!");
        }
        categoryService.delete(categoryId);
    }
}

