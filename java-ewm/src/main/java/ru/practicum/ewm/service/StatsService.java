package ru.practicum.ewm.service;

import org.springframework.stereotype.Service;

@Service
public interface StatsService {

    Long getViews(String uri);

    void setHits(String uri, String ip);
}
