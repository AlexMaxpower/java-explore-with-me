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

    @PatchMapping("/{commentId}/publish")
    public CommentDto publish(@PathVariable Long commentId, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на публикацию комментария с ID={}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                commentId);
        return commentService.publishComment(commentId);
    }

    @PatchMapping("/{commentId}/reject")
    public CommentDto reject(@PathVariable Long commentId, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на отклонение комментария с ID={}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                commentId);
        return commentService.rejectComment(commentId);
    }

    @PatchMapping
    public CommentDto update(@Valid @RequestBody CommentDto commentDto,
                             HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на редактирование комментария {}",
                request.getRemoteAddr(), request.getRequestURI(), commentDto);
        return commentService.updateCommentByAdmin(commentDto);
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
                DateTimeFormatter.ofPattern(dateTimeFormat)) : LocalDateTime.now().minusYears(300);

        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd,
                DateTimeFormatter.ofPattern(dateTimeFormat)) : LocalDateTime.now().plusYears(300);

        return commentService.getCommentsForAdmin(text, events, status, start, end, from, size);
    }

}
