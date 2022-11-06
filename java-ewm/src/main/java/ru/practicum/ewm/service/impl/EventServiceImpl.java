package ru.practicum.ewm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventCreateDto;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.NotValidException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.other.EventSort;
import ru.practicum.ewm.other.Pager;
import ru.practicum.ewm.other.State;
import ru.practicum.ewm.request.AdminUpdateEventRequest;
import ru.practicum.ewm.request.UpdateEventRequest;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.storage.EventRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService, Pager {

    private final EventRepository repository;
    private final EventMapper mapper;
    private final EntityManager entityManager;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper,
                            EntityManager entityManager) {
        this.repository = eventRepository;
        this.mapper = eventMapper;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public EventFullDto create(EventCreateDto eventCreateDto) {
        if (eventCreateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new NotValidException("Дата и время события не могут быть раньше, чем через два" +
                    " часа от текущего момента");
        }
        Event event = repository.save(mapper.eventCreateDtoToEvent(eventCreateDto));
        return mapper.eventToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto cancel(Long eventId, Long userId) {
        Event event = getEvent(eventId);

        if (!userId.equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Только создатель события может его отменить.");
        }

        if (State.PENDING.equals(event.getState())) {
            event.setState(State.CANCELED);
        } else {
            throw new NotValidException("Only pending or canceled events can be changed");
        }

        event = repository.save(event);
        return mapper.eventToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto publish(Long eventId) {
        Event event = getEvent(eventId);

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new NotValidException("Дата и время события не могут быть раньше, чем через час" +
                    " от текущего момента");
        }

        if (State.PENDING.equals(event.getState())) {
            event.setState(State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else {
            throw new NotValidException("Only pending events can be changed");
        }
        event = repository.save(event);
        return mapper.eventToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto reject(Long eventId) {
        Event event = getEvent(eventId);

        if (State.PENDING.equals(event.getState())) {
            event.setState(State.CANCELED);
        } else {
            throw new NotValidException("Only pending events can be changed");
        }
        event = repository.save(event);
        return mapper.eventToEventFullDto(event);
    }

    @Override
    public EventFullDto getEventByIdForPublic(Long eventId) {
        Event event = getEvent((eventId));

        if (!State.PUBLISHED.equals(event.getState())) {
            throw new ForbiddenException("Event not published.");
        }
        return mapper.eventToEventFullDto(event);
    }

    @Override
    public EventShortDto getEventShortDtoByIdForPublic(Long eventId) {
        Event event = getEvent(eventId);

        if (!State.PUBLISHED.equals(event.getState())) {
            throw new ForbiddenException("Event not published.");
        }
        return mapper.eventToEventShortDto(event);
    }

    @Override
    public EventFullDto getEventByIdForUser(Long eventId, Long userId) {

        Event event = getEvent(eventId);

        if (!userId.equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Event with id=" + eventId + " created other user");
        }
        return mapper.eventToEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        Pageable page = getPage(from, size, "id", Sort.Direction.ASC);
        return repository.findByInitiatorId(userId, page).stream()
                .map(mapper::eventToEventShortDto)
                .collect(toList());
    }

    @Override
    public Event getEvent(Long eventId) {
        return repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found."));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, UpdateEventRequest updateEventRequest, Category category) {

        Event event = getEvent(updateEventRequest.getEventId());
        log.info("Получено событие для редактирования {}", event);
        if (!userId.equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Отредактировать событие может только инициатор!");
        }

        if (event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException("Only pending or canceled events can be changed");
        }

        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }

        if (updateEventRequest.getCategoryId() != null) {
            event.setCategory(category);
        }

        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }

        if (updateEventRequest.getEventDate() != null) {
            event.setEventDate(updateEventRequest.getEventDate());
        }

        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }

        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }

        if (updateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventRequest.getRequestModeration());
        }

        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }

        event.setState(State.PENDING);
        event = repository.save(event);

        return mapper.eventToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest,
                                           Category category) {

        Event event = getEvent(eventId);
        log.info("Получено событие для редактирования {}", event);

        if (adminUpdateEventRequest.getAnnotation() != null) {
            event.setAnnotation(adminUpdateEventRequest.getAnnotation());
        }

        if (adminUpdateEventRequest.getCategoryId() != null) {
            event.setCategory(category);
        }

        if (adminUpdateEventRequest.getDescription() != null) {
            event.setDescription(adminUpdateEventRequest.getDescription());
        }

        if (adminUpdateEventRequest.getEventDate() != null) {
            event.setEventDate(adminUpdateEventRequest.getEventDate());
        }

        if (adminUpdateEventRequest.getLocation() != null) {
            event.setLon(adminUpdateEventRequest.getLocation().getLon());
            event.setLat(adminUpdateEventRequest.getLocation().getLat());
        }

        if (adminUpdateEventRequest.getPaid() != null) {
            event.setPaid(adminUpdateEventRequest.getPaid());
        }

        if (adminUpdateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(adminUpdateEventRequest.getParticipantLimit());
        }

        if (adminUpdateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(adminUpdateEventRequest.getRequestModeration());
        }

        if (adminUpdateEventRequest.getTitle() != null) {
            event.setTitle(adminUpdateEventRequest.getTitle());
        }

        event = repository.save(event);

        return mapper.eventToEventFullDto(event);
    }

    public List<EventShortDto> getEventsForPublic(String text, Set<Long> categories, Boolean paid,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable, EventSort sort, Integer from, Integer size) {

        if (rangeStart.isAfter(rangeEnd)) {
            throw new NotValidException("Дата и время окончаний события не может быть раньше даты начала событий!");
        }

        Session session = entityManager.unwrap(Session.class);

        // в выдаче должны быть только опубликованные события
        session.enableFilter("stateFilter").setParameter("state", State.PUBLISHED.toString());

        // включаем фильтр по датам
        Filter dateFilter = session.enableFilter("dateFilter");
        dateFilter.setParameter("rangeStart", rangeStart);
        dateFilter.setParameter("rangeEnd", rangeEnd);

        // включаем фильтр по платным/бесплатным событиям, если задано
        if (paid != null) {
            session.enableFilter("paidFilter").setParameter("paid", paid);
        }

        List<Event> events;

        if (categories != null) {
            events = repository.findByCategoryIdsAndText(text, categories);
        } else {
            events = repository.findByText(text);
        }


        // выключаем фильтры
        if (paid != null) session.disableFilter("paidFilter");

        session.disableFilter("dateFilter");
        session.disableFilter("stateFilter");

        List<EventShortDto> eventShortDtos = events.stream()
                .map(mapper::eventToEventShortDto)
                .collect(toList());

        if (onlyAvailable) {
            eventShortDtos = eventShortDtos.stream()
                    .filter(x -> x.getConfirmedRequests() < x.getParticipantLimit())
                    .collect(toList());
        }

        if (EventSort.VIEWS.equals(sort)) {
            eventShortDtos = eventShortDtos.stream()
                    .sorted(Comparator.comparingLong(EventShortDto::getViews))
                    .skip(from)
                    .limit(size)
                    .collect(toList());
        } else {
            eventShortDtos = eventShortDtos.stream()
                    .sorted(Comparator.comparing(EventShortDto::getEventDate))
                    .skip(from)
                    .limit(size)
                    .collect(toList());
        }
        return eventShortDtos;
    }

    public List<EventFullDto> getEventsForAdmin(Set<Long> users, Set<State> states, Set<Long> categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                Integer from, Integer size) {

        if (rangeStart.isAfter(rangeEnd)) {
            throw new NotValidException("Дата и время окончаний события не может быть раньше даты начала событий!");
        }

        if (states == null) {
            states = new HashSet<>();
            states.add(State.PENDING);
            states.add(State.CANCELED);
            states.add(State.PUBLISHED);
        }

        Session session = entityManager.unwrap(Session.class);

        // включаем фильтр по датам
        Filter dateFilter = session.enableFilter("dateFilter");
        dateFilter.setParameter("rangeStart", rangeStart);
        dateFilter.setParameter("rangeEnd", rangeEnd);

        List<Event> events = new ArrayList<>();

        Pageable page = getPage(from, size, "id", Sort.Direction.ASC);

        if ((categories != null) && (users != null)) {
            events = repository.findByUsersAndCategoriesAndStates(users, categories, states, page);
        }

        if ((categories == null) && (users == null)) {
            events = repository.findByStates(states, page);
        }

        if ((categories != null) && (users == null)) {
            events = repository.findByCategoriesAndStates(categories, states, page);
        }

        if ((categories == null) && (users != null)) {
            events = repository.findByUsersAndStates(users, states, page);
        }

        session.disableFilter("dateFilter");

        List<EventFullDto> eventFullDtos = events.stream()
                .map(mapper::eventToEventFullDto)
                .collect(toList());

        return eventFullDtos;
    }

    public List<EventShortDto> getEventsByCategoryId(Long categoryId) {
        return repository.findByCategoryId(categoryId).stream()
                .map(mapper::eventToEventShortDto)
                .collect(toList());
    }
}