package ru.practicum.ewm.other;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Location {

    private Double lat;
    private Double lon;

}