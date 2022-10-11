package ru.practicum.ewm.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EventCreateDto;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.other.EventSort;
import ru.practicum.ewm.other.State;
import ru.practicum.ewm.request.AdminUpdateEventRequest;
import ru.practicum.ewm.request.UpdateEventRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public interface EventService {
    EventFullDto create(EventCreateDto eventCreateDto);

    EventFullDto cancel(Long eventId, Long userId);

    EventFullDto publish(Long eventId);

    EventFullDto reject(Long eventId);

    EventFullDto getEventByIdForPublic(Long eventId);

    EventShortDto getEventShortDtoByIdForPublic(Long eventId);

    EventFullDto getEventByIdForUser(Long eventId, Long userId);

    Event getEvent(Long eventId);

    List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest, Category category);

    EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest, Category category);

    List<EventShortDto> getEventsForPublic(String text, Set<Long> categories, Boolean paid,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                           Boolean onlyAvailable, EventSort sort, Integer from, Integer size);

    List<EventFullDto> getEventsForAdmin(Set<Long> users, Set<State> states, Set<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Integer from, Integer size);
}
