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

    CommentDto updateComment(Long userId, Long commentId, CommentDto commentDto);

    CommentDto updateCommentByAdmin(Long commentId, CommentDto commentDto);

    List<CommentDto> getCommentsByUserId(Long userId);

    CommentDto getCommentById(Long commentId, Long userId);

    List<CommentDto> getComments(String text, Set<Long> events, Status status, LocalDateTime start,
                                         LocalDateTime end, Integer from, Integer size);

    Comment getComment(Long commentId);
}
