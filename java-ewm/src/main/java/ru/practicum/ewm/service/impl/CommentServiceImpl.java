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
        if (comment.getEvent().getState().equals(State.PUBLISHED)) {
            comment = repository.save(mapper.commentDtoToComment(commentDto));
            log.info("Комментарий создан {}", comment);
        } else {
            throw new ForbiddenException("Разместить комментарий можно только к опубликованному событию!");
        }
        return mapper.commentToCommentDto(comment);
    }

    @Override
    public CommentDto cancelComment(Long userId, Long commentId) {
        Comment comment = getComment(commentId);
        log.info("Получен комментарий {}", comment);

        if (userId.equals(comment.getCommentator().getId())) {
            comment.setStatus(Status.CANCELED);
            comment = repository.save(comment);
            log.info("Комментарий снят с публикации {}", comment);
        } else {
            throw new ForbiddenException("Отменить публикацию комментария может только сам комментатор!");
        }
        return mapper.commentToCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long userId, CommentDto commentDto) {
        Comment comment = getComment(commentDto.getId());
        log.info("Получен комментарий {}", comment);

        if (userId.equals(comment.getCommentator().getId())) {
            comment.setText(commentDto.getText());
            comment.setCreated(LocalDateTime.now());
            comment.setStatus(Status.PENDING);
            comment = repository.save(comment);
            log.info("Комментарий отредактирован {}", comment);
        } else {
            throw new ForbiddenException("Редактировать комментарий может только сам комментатор!");
        }
        return mapper.commentToCommentDto(comment);
    }

    @Override
    public CommentDto updateCommentByAdmin(CommentDto commentDto) {
        Comment comment = getComment(commentDto.getId());
        log.info("Получен комментарий {}", comment);

        comment.setText(commentDto.getText());
        comment.setStatus(Status.CONFIRMED);
        comment = repository.save(comment);
        log.info("Комментарий отредактирован администратором {}", comment);

        return mapper.commentToCommentDto(comment);
    }


    @Override
    public CommentDto publishComment(Long commentId) {
        Comment comment = getComment(commentId);
        log.info("Получен комментарий {}", comment);

        if (Status.PENDING.equals(comment.getStatus())) {
            comment.setStatus(Status.CONFIRMED);
        } else {
            throw new NotValidException("Only pending comment can be changed");
        }
        comment = repository.save(comment);
        log.info("Комментарий одобрен и опубликован {}", comment);
        return mapper.commentToCommentDto(comment);
    }

    @Override
    public CommentDto rejectComment(Long commentId) {
        Comment comment = getComment(commentId);
        log.info("Получен комментарий {}", comment);
        comment.setStatus(Status.REJECTED);
        comment = repository.save(comment);
        log.info("Комментарий отклонен и снят с публикации {}", comment);
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
    public List<CommentDto> getCommentsForPublic(String text, Set<Long> events, LocalDateTime start,
                                                 LocalDateTime end, Integer from, Integer size) {

        if (start.isAfter(end)) {
            throw new NotValidException("Дата и время окончаний комментариев не может быть" +
                    " раньше даты начала комментариев!");
        }

        Session session = entityManager.unwrap(Session.class);

        // в выдаче должны быть только опубликованные комментарии
        session.enableFilter("statusComFilter").setParameter("status", Status.CONFIRMED.toString());

        // включаем фильтр по датам
        Filter dateFilter = session.enableFilter("dateComFilter");
        dateFilter.setParameter("rangeStart", start);
        dateFilter.setParameter("rangeEnd", end);

        Pageable page = getPage(from, size, "created", Sort.Direction.ASC);

        Filter eventFilter;

        if (events != null) {
            eventFilter = session.enableFilter("eventsComFilter");
            eventFilter.setParameterList("eventIds", events);
        }

        List<Comment> comments = repository.findByText(text, page);

        if (events != null) {
            session.disableFilter("eventsComFilter");
        }

        // выключаем фильтры

        session.disableFilter("dateComFilter");
        session.disableFilter("statusComFilter");

        return comments.stream()
                .map(mapper::commentToCommentDto)
                .collect(toList());
    }

    @Override
    public List<CommentDto> getCommentsForAdmin(String text, Set<Long> events, Status status,
                                                LocalDateTime start, LocalDateTime end, Integer from, Integer size) {

        if (start.isAfter(end)) {
            throw new NotValidException("Дата и время окончаний комментариев не может быть" +
                    " раньше даты начала комментариев!");
        }

        Session session = entityManager.unwrap(Session.class);

        // включаем фильтр по датам
        Filter dateFilter = session.enableFilter("dateComFilter");
        dateFilter.setParameter("rangeStart", start);
        dateFilter.setParameter("rangeEnd", end);

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

        session.disableFilter("dateComFilter");

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
}
