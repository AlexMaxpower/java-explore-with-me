package ru.practicum.ewm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.StatsClient;

import ru.practicum.ewm.other.EndpointHit;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.other.ViewStats;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatsServiceImpl implements StatsService {
    private static final String APP_NAME = "ewm-service";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsClient client;


    @Autowired
    public StatsServiceImpl(StatsClient statsClient) {
        this.client = statsClient;
    }

    @Override
    public Long getViews(String uri) {
        String start = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC).format(formatter);
        String end = LocalDateTime.now().plusYears(1000).format(formatter);
        List<ViewStats> listStats;

        log.info("Делаем запрос на сервер статистики для {}", uri);
        listStats = client.getStats(start, end, new String[]{uri}, false);
        log.info("Получен ответ от сервера статистики {}", listStats);

        if (listStats != null && listStats.size() > 0) {
            listStats = listStats.stream()
                    .filter(x -> APP_NAME.equals(x.getApp()))
                    .collect(Collectors.toList());
            return listStats.size() > 0 ? listStats.get(0).getViews() : 0L;
        } else {
            return 0L;
        }
    }

    @Override
    public void setHits(String uri, String ip) {

        EndpointHit endpointHit = new EndpointHit(null, APP_NAME, uri, ip, LocalDateTime.now());

        log.info("Отправляем запрос на сервер статистики для {}", uri);
        EndpointHitDto endpointHitDto = client.setStat(endpointHit);
        log.info("Получен ответ от сервера статистики {}", endpointHitDto);
    }
}
