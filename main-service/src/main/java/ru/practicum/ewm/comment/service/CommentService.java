package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(CommentDto commentDto, Long userId, Long eventId);

    CommentDto updateComment(UpdateCommentDto commentDto, Long userId, Long commentId);

    CommentDto getCommentByAdmin(Long commentId);

    CommentDto getCommentByAuthor(Long userId, Long commentId);

    List<CommentDto> getAllComments(Long eventId, Integer from, Integer size);

    List<CommentDto> getAllCommentsByAuthor(Long authorId, Integer from, Integer size);

    void deleteComment(Long commentId);

    void deleteCommentByAuthor(Long userId, Long commentId);
}
