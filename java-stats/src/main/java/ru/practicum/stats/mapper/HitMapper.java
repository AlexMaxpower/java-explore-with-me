package ru.practicum.stats.mapper;

import org.mapstruct.Mapper;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.entity.Hit;

@Mapper(componentModel = "spring")
public interface HitMapper {

    EndpointHit hitToEndpointHit(Hit hit);

    Hit endpointHitToHit(EndpointHit endpointHit);

    ViewStats hitDtoToViewStats(HitDto hitDto);

}