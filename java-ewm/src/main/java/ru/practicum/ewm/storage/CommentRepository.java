package ru.practicum.ewm.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.entity.Comment;
import java.util.List;
import java.util.Set;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByCommentatorIdOrderByCreated(Long commentatorId);

    @Query("SELECT c FROM Comment AS c " +
            "WHERE LOWER(c.text) LIKE CONCAT('%',LOWER(:text),'%')")
    List<Comment> findByText(String text, Pageable pageable);

    @Query("SELECT c FROM Comment AS c " +
            "WHERE (c.event.id IN :eventsIds) AND " +
            "(LOWER(c.text) LIKE CONCAT('%',LOWER(:text),'%'))")
    List<Comment> findByTextAndEventsIds(String text, Set<Long> eventsIds, Pageable pageable);
}