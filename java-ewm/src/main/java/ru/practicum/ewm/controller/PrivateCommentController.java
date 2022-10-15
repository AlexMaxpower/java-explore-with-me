package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.service.CommentService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class PrivateCommentController {
    private final CommentService commentService;

    @Autowired
    public PrivateCommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @PostMapping("/{userId}/comments")
    public CommentDto create(@Valid @RequestBody CommentDto commentDto,
                             @PathVariable Long userId,
                             HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на добавление комментария к событию с ID={}" +
                        " от пользователя с ID={}",
                request.getRemoteAddr(), request.getRequestURI(), commentDto.getEventId(), userId);
        return commentService.createComment(userId, commentDto);
    }

    @PatchMapping("/{userId}/comments/{commentId}/cancel")
    public CommentDto cancel(@PathVariable Long userId, @PathVariable Long commentId, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на отмену комментария с ID={} от пользователя с ID={}",
                request.getRemoteAddr(), request.getRequestURI(), commentId, userId);
        return commentService.cancelComment(userId, commentId);
    }

    @PatchMapping("/{userId}/comments")
    public CommentDto update(@Valid @RequestBody CommentDto commentDto, @PathVariable Long userId,
                             HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на редактирование комментария {} от пользователя с ID={}",
                request.getRemoteAddr(), request.getRequestURI(), commentDto, userId);
        return commentService.updateComment(userId, commentDto);
    }

    @GetMapping("/{userId}/comments")
    public List<CommentDto> getCommentsByUserId(@PathVariable Long userId,
                                                             HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на получение комментариев от пользователя с ID={}",
                request.getRemoteAddr(), request.getRequestURI(), userId);
        return commentService.getCommentsByUserId(userId);
    }

    @GetMapping("/{userId}/comments/{commentId}")
    public CommentDto getCommentById(@PathVariable Long userId,
                                         @PathVariable Long commentId,
                                         HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' пользователем с ID={} на получения комментария с ID={}",
                request.getRemoteAddr(), request.getRequestURI(), userId, commentId);
        return commentService.getCommentById(commentId, userId);
    }

}



