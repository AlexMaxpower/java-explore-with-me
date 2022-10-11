package ru.practicum.ewm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.entity.Compilation;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.exception.AlreadyExistsException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.other.Pager;
import ru.practicum.ewm.service.CompilationService;
import ru.practicum.ewm.storage.CompilationRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CompilationServiceImpl implements CompilationService, Pager {
    private final CompilationRepository repository;
    private final CompilationMapper mapper;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, CompilationMapper compilationMapper) {
        this.repository = compilationRepository;
        this.mapper = compilationMapper;
    }

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        try {
            Compilation compilation = mapper.newCompilationDtoToCompilation(
                    newCompilationDto);
            compilation = repository.save(compilation);
            log.info("Создана подборка: {}", compilation.toString());
            return mapper.compilationToCompilationDto(compilation);

        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException("Подборка с названием '" +
                    newCompilationDto.getTitle() + "' уже существует!");
        }
    }

    @Override
    public CompilationDto pinUnpin(Long compId, boolean pin) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found."));
        compilation.setPinned(pin);
        return mapper.compilationToCompilationDto(repository.save(compilation));
    }

    @Override
    public void delete(Long compId) {
        try {
            repository.deleteById(compId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Compilation with id=" + compId + " was not found.");
        }
    }

    @Override
    public void deleteEvent(Long compId, Event event) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found."));
        compilation.getEvents().remove(event);
        repository.save(compilation);
    }

    @Override
    public CompilationDto addEvent(Long compId, Event event) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found."));
        compilation.getEvents().add(event);
        return mapper.compilationToCompilationDto(repository.save(compilation));
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found."));
        return mapper.compilationToCompilationDto(compilation);
    }

    @Override
    public Collection<CompilationDto> getCompilations(Integer from, Integer size, Boolean pinned) {
        Pageable page = getPage(from, size, "id", Sort.Direction.ASC);
        if (pinned != null) {
            return repository.findAllByPinned(pinned, page).stream()
                    .map(mapper::compilationToCompilationDto)
                    .collect(Collectors.toList());
        } else {
            return repository.findAll(page).stream()
                    .map(mapper::compilationToCompilationDto)
                    .collect(Collectors.toList());
        }
    }
}
