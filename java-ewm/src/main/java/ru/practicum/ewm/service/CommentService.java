package ru.practicum.ewm.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.entity.Comment;
import ru.practicum.ewm.other.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public interface CommentService {

    CommentDto createComment(Long userId, CommentDto commentDto);

    CommentDto cancelComment(Long userId, Long commentId);

    CommentDto updateComment(Long userId, CommentDto commentDto);

    CommentDto updateCommentByAdmin(CommentDto commentDto);

    CommentDto publishComment(Long commentId);

    CommentDto rejectComment(Long commentId);

    List<CommentDto> getCommentsByUserId(Long userId);

    CommentDto getCommentById(Long commentId, Long userId);

    List<CommentDto> getCommentsForPublic(String text, Set<Long> events, LocalDateTime start,
                                          LocalDateTime end, Integer from, Integer size);

    List<CommentDto> getCommentsForAdmin(String text, Set<Long> events, Status status, LocalDateTime start,
                                         LocalDateTime end, Integer from, Integer size);

    Comment getComment(Long commentId);
}
