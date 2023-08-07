package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> getAllByAuthorId(Long authorId, Pageable pageable);

    List<Comment> getAllByEventId(Long eventId, Pageable pageable);

    Comment getByTextAndAuthorIdAndEventId(String text, Long authorId, Long eventId);

}
