package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.entity.ParticipationRequest;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "event.id", target = "eventId")
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);

}