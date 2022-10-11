package ru.practicum.ewm.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.entity.Compilation;
import ru.practicum.ewm.service.EventService;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CompilationMapper {

    private EventMapper eventMapper;
    private EventService eventService;

    @Autowired
    public CompilationMapper(EventMapper eventMapper, EventService eventService) {
        this.eventMapper = eventMapper;
        this.eventService = eventService;
    }

    public Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto) {

        Compilation compilation = Compilation.builder()
                .id(null)
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .events(newCompilationDto.getEvents().stream()
                        .filter(Objects::nonNull)
                        .map(eventService::getEvent)
                        .collect(Collectors.toSet()))
                .build();
        return compilation;
    }

    public CompilationDto compilationToCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(compilation.getEvents().stream()
                        .map(eventMapper::eventToEventShortDto)
                        .collect(Collectors.toSet()))
                .build();
        return compilationDto;
    }
}
