package ru.practicum.stats.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.entity.Hit;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.service.StatsService;
import ru.practicum.stats.storage.HitRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final HitMapper mapper;
    private final HitRepository repository;

    public StatsServiceImpl(HitMapper hitMapper, HitRepository hitRepository) {
        this.mapper = hitMapper;
        this.repository = hitRepository;
    }

    @Override
    @Transactional
    public EndpointHit create(EndpointHit endpointHit) {
        Hit hit = mapper.endpointHitToHit(endpointHit);
        hit = repository.save(hit);
        log.info("Просмотр {} записан в базу", hit);
        return mapper.hitToEndpointHit(hit);
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<HitDto> viewStats;

        if (unique) {
            viewStats = repository.calculateUniqueHits(uris, start, end);
            log.info("Статистика уникальных просмотров получена");
        } else {
            viewStats = repository.calculateHits(uris, start, end);
            log.info("Статистика просмотров получена");
        }

        return viewStats.stream()
                .map(mapper::hitDtoToViewStats)
                .collect(Collectors.toList());
    }
}
