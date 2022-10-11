package ru.practicum.ewm.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.other.Status;

import java.util.List;

@Service
public interface RequestService {

    List<ParticipationRequestDto> getRequestByUserId(Long userId);

    List<ParticipationRequestDto> getRequestByEventIdAndStatus(Long eventId, Status status);

    ParticipationRequestDto getRequestByUserIdAndEventId(Long userId, Long eventId);

    ParticipationRequestDto createRequest(User user, Event event);

    ParticipationRequestDto cancelRequest(User user, Long requestId);

    List<ParticipationRequestDto> getRequestsByUserId(Long userId);

    ParticipationRequestDto confirmRequest(Event event, Long requestId);

    ParticipationRequestDto rejectRequest(Event event, Long requestId);

    List<ParticipationRequestDto> getRequestsByEventId(Long eventId);
}
