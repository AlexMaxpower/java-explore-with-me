package ru.practicum.ewm.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.other.Location;
import ru.practicum.ewm.other.State;
import ru.practicum.ewm.other.Status;
import ru.practicum.ewm.service.RequestService;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;


@Component
public class EventMapper {

    private final StatsService statsService;
    private final RequestService requestService;

    @Autowired
    public EventMapper(StatsService statsService, RequestService requestService) {
        this.statsService = statsService;
        this.requestService = requestService;
    }

    public EventCreateDto newEventDtoToEventCreateDto(NewEventDto newEventDto,
                                                      UserDto initiator, CategoryDto categoryDto) {
        if (newEventDto == null) {
            return null;
        }

        EventCreateDto eventCreateDto = EventCreateDto.builder()
                .annotation(newEventDto.getAnnotation())
                .categoryDto(categoryDto)
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(initiator)
                .paid(newEventDto.isPaid())
                .location(newEventDto.getLocation())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .title(newEventDto.getTitle())
                .createdOn(LocalDateTime.now())
                .build();
        return eventCreateDto;
    }

    public Event eventCreateDtoToEvent(EventCreateDto eventCreateDto) {
        if (eventCreateDto == null) {
            return null;
        }

        Event event = Event.builder()
                .id(null)
                .title(eventCreateDto.getTitle())
                .annotation(eventCreateDto.getAnnotation())
                .category(new Category(eventCreateDto.getCategoryDto().getId(),
                        eventCreateDto.getCategoryDto().getName()))
                .description(eventCreateDto.getDescription())
                .eventDate(eventCreateDto.getEventDate())
                .lon(eventCreateDto.getLocation().getLon())
                .lat(eventCreateDto.getLocation().getLat())
                .initiator(new User(eventCreateDto.getInitiator().getId(),
                        eventCreateDto.getInitiator().getEmail(),
                        eventCreateDto.getInitiator().getName()))
                .createdOn(eventCreateDto.getCreatedOn())
                .paid(eventCreateDto.isPaid())
                .participantLimit(eventCreateDto.getParticipantLimit())
                .requestModeration(eventCreateDto.isRequestModeration())
                .state(State.PENDING)
                .build();
        return event;
    }

    public EventFullDto eventToEventFullDto(Event event) {

        Long confirmedRequests =
                (long) requestService.getRequestByEventIdAndStatus(event.getId(), Status.CONFIRMED).size();
        Long views = statsService.getViews("/events/" + event.getId());

        EventFullDto eventFullDto = EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .categoryDto(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .location(new Location(event.getLat(), event.getLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(views)
                .build();

        return eventFullDto;
    }

    public EventShortDto eventToEventShortDto(Event event) {

        Long confirmedRequests =
                (long) requestService.getRequestByEventIdAndStatus(event.getId(), Status.CONFIRMED).size();
        Long views = statsService.getViews("/events/" + event.getId());

        EventShortDto eventShortDto = EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .categoryDto(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .participantLimit(event.getParticipantLimit())
                .build();

        return eventShortDto;
    }
}
