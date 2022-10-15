package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.entity.Event;

import java.util.Collection;

public interface CompilationService {

    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto pinUnpin(Long compId, boolean pin);

    void delete(Long compId);

    void deleteEvent(Long compId, Event event);

    CompilationDto addEvent(Long compId, Event event);

    CompilationDto getCompilationById(Long compId);

    Collection<CompilationDto> getCompilations(Integer from, Integer size, Boolean pinned);
}
