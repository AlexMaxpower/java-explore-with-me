package ru.practicum.ewm.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.ewm.other.EndpointHit;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.other.ViewStats;

import java.util.List;

@FeignClient(value = "stats", url = "${feign.url}")
public interface StatsClient {

    @GetMapping("/stats?start={start}&end={end}&uris={uris}&unique={unique}")
    List<ViewStats> getStats(@PathVariable String start, @PathVariable String end,
                             @PathVariable String[] uris, @PathVariable boolean unique);

    @PostMapping("/hit")
    EndpointHitDto setStat(@RequestBody EndpointHit endpointHit);
}