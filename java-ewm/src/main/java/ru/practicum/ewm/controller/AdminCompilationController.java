package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.facade.UserEventFacade;
import ru.practicum.ewm.service.CompilationService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;
    private final UserEventFacade userEventFacade;


    @Autowired
    public AdminCompilationController(CompilationService compilationService, UserEventFacade userEventFacade) {
        this.compilationService = compilationService;
        this.userEventFacade = userEventFacade;
    }

    @PostMapping()
    public CompilationDto create(@Valid @RequestBody NewCompilationDto newCompilationDto, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на добавление подборки {}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                newCompilationDto.toString());
        return compilationService.create(newCompilationDto);
    }

    @PatchMapping("/{compId}/pin")
    public CompilationDto pin(@PathVariable Long compId, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на закрепление на главной подборки с ID={}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                compId);
        return compilationService.pinUnpin(compId, true);
    }

    @DeleteMapping("/{compId}/pin")
    public CompilationDto unpin(@PathVariable Long compId, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на открепление на главной подборки с ID={}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                compId);
        return compilationService.pinUnpin(compId, false);
    }

    @DeleteMapping("/{compId}")
    public void delete(@PathVariable Long compId, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на удаление подборки с ID={}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                compId);
        compilationService.delete(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventFromComp(@PathVariable Long compId, @PathVariable Long eventId, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на удаление события с ID={} из подборки с ID={}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                eventId,
                compId);
        userEventFacade.deleteEventFromComp(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public CompilationDto addEventToComp(@PathVariable Long compId, @PathVariable Long eventId, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на добавление события с ID={} в подборку с ID={}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                eventId,
                compId);
        return userEventFacade.addEventToComp(compId, eventId);
    }
}