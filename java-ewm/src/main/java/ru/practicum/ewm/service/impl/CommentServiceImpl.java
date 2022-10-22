package ru.practicum.ewm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.entity.Comment;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.NotValidException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.other.Pager;
import ru.practicum.ewm.other.State;
import ru.practicum.ewm.other.Status;
import ru.practicum.ewm.service.CommentService;
import ru.practicum.ewm.storage.CommentRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService, Pager {

    private final CommentRepository repository;
    private final CommentMapper mapper;
    private final EntityManager entityManager;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, CommentMapper commentMapper,
                              EntityManager entityManager) {
        this.repository = commentRepository;
        this.mapper = commentMapper;
        this.entityManager = entityManager;
    }

    @Override
    public CommentDto createComment(Long userId, CommentDto commentDto) {
        commentDto.setCommentatorId(userId);
        commentDto.setCreated(LocalDateTime.now());
        commentDto.setStatus(Status.PENDING);
        log.info("Создаем новый комментарий {}", commentDto);
        Comment comment = mapper.commentDtoToComment(commentDto);
        if (!comment.getEvent().getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException("Разместить комментарий можно только к опубликованному событию!");

        }
        comment = repository.save(mapper.commentDtoToComment(commentDto));
        log.info("Комментарий создан {}", comment);
        return mapper.commentToCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, CommentDto commentDto) {
        Comment comment = getComment(commentId);
        log.info("Получен комментарий {}", comment);

        if (!userId.equals(comment.getCommentator().getId())) {
            throw new ForbiddenException("Редактировать комментарий может только сам комментатор!");
        }

        if (commentDto.getStatus() != null) {
            comment.setStatus(commentDto.getStatus());
        }

        if (commentDto.getText() != null) {
            comment.setText(commentDto.getText());
            comment.setStatus(Status.PENDING);
        }

        isValid(comment);
        comment = repository.save(comment);
        log.info("Комментарий отредактирован {}", comment);

        return mapper.commentToCommentDto(comment);
    }

    @Override
    public CommentDto updateCommentByAdmin(Long commentId, CommentDto commentDto) {
        Comment comment = getComment(commentId);
        log.info("Получен комментарий {}", comment);

        if (commentDto.getStatus() != null) {
            comment.setStatus(commentDto.getStatus());
        }

        if (commentDto.getText() != null) {
            comment.setText(commentDto.getText());
            comment.setStatus(Status.CONFIRMED);
        }

        isValid(comment);
        comment = repository.save(comment);
        log.info("Комментарий отредактирован администратором {}", comment);

        return mapper.commentToCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByUserId(Long userId) {
        return repository.findAllByCommentatorIdOrderByCreated(userId).stream()
                .map(mapper::commentToCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long commentId, Long userId) {
        Comment comment = getComment(commentId);
        log.info("Получен комментарий {}", comment);
        if (!userId.equals(comment.getCommentator().getId())) {
            throw new ForbiddenException("Comment with id=" + commentId + " created other user");
        }
        return mapper.commentToCommentDto(comment);
    }

    @Override
    public List<CommentDto> getComments(String text, Set<Long> events, Status status,
                                        LocalDateTime start, LocalDateTime end, Integer from, Integer size) {

        Session session = entityManager.unwrap(Session.class);

        Filter dateFilter = null;

        if ((start != null) && (end != null)) {
            if (start.isAfter(end)) {
                throw new NotValidException("Дата и время окончаний комментариев не может быть" +
                        " раньше даты начала комментариев!");
            }

            // включаем фильтр по датам начала и конца
            dateFilter = session.enableFilter("dateAllComFilter");
            dateFilter.setParameter("rangeStart", start);
            dateFilter.setParameter("rangeEnd", end);
        }

        if ((start != null) && (end == null)) {
            dateFilter = session.enableFilter("dateStartComFilter");
            dateFilter.setParameter("rangeStart", start);
        }

        if ((start == null) && (end != null)) {
            dateFilter = session.enableFilter("dateEndComFilter");
            dateFilter.setParameter("rangeEnd", end);
        }

        if (status != null) {
            // включаем фильтр по статусу комментария
            session.enableFilter("statusComFilter").setParameter("status", status.toString());
        }

        Pageable page = getPage(from, size, "created", Sort.Direction.ASC);

        Filter eventFilter;

        if (events != null) {
            eventFilter = session.enableFilter("eventsComFilter");
            eventFilter.setParameterList("eventIds", events);
        }

        List<Comment> comments = repository.findByText(text, page);

        // выключаем фильтры
        if (events != null) {
            session.disableFilter("eventsComFilter");
        }

        if (dateFilter != null) {
            session.disableFilter(dateFilter.getName());
        }

        if (status != null) {
            session.disableFilter("statusComFilter");
        }

        return comments.stream()
                .map(mapper::commentToCommentDto)
                .collect(toList());
    }

    @Override
    public Comment getComment(Long commentId) {
        return repository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " not found."));
    }

    private Boolean isValid(Comment comment) {
        if (comment.getText() == null) {
            throw new NotValidException("Должен быть комментарий!");
        }
        if ((comment.getText().length() < 3) || (comment.getText().length() > 2000)) {
            throw new NotValidException("Длина комментария должна быть больше 3 символов, но меньше 2000 символов");
        }
        return true;
    }
}
