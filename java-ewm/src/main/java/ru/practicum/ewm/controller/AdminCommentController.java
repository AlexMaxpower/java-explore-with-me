package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.other.Status;
import ru.practicum.ewm.service.CommentService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(path = "/admin/comments")
public class AdminCommentController {

    @Value("${format.pattern.datetime}")
    private String dateTimeFormat;

    private final CommentService commentService;

    @Autowired
    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@RequestBody CommentDto commentDto, @PathVariable Long commentId,
                             HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на редактирование комментария с ID={}: {}",
                request.getRemoteAddr(), request.getRequestURI(), commentId, commentDto);
        return commentService.updateCommentByAdmin(commentId, commentDto);
    }

    @GetMapping
    public Collection<CommentDto> getComments(@RequestParam(required = false, defaultValue = "") String text,
                                              @RequestParam(required = false) Set<Long> events,
                                              @RequestParam(required = false) String rangeStart,
                                              @RequestParam(required = false) String rangeEnd,
                                              @RequestParam(required = false) Status status,
                                              @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Valid @Positive @RequestParam(defaultValue = "10") Integer size,
                                              HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на получение списка комментариев",
                request.getRemoteAddr(), request.getRequestURI());

        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart,
                DateTimeFormatter.ofPattern(dateTimeFormat)) : null;

        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd,
                DateTimeFormatter.ofPattern(dateTimeFormat)) : null;

        return commentService.getComments(text, events, status, start, end, from, size);
    }

}
