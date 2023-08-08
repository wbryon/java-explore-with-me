package ru.practicum.ewm.comment.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.WrongRequestException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    public CommentServiceImpl(UserRepository userRepository, EventRepository eventRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long userId, Long eventId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id: " + eventId + " не найдено"));
        if (commentRepository.getByTextAndAuthorIdAndEventId(commentDto.getText(), author.getId(), eventId) != null)
            throw new WrongRequestException("Для одного события пользователь может создать только один комментарий");
        Comment comment = CommentMapper.toComment(commentDto, author, event);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(CommentDto commentDto, Long userId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Comment oldComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id: " + commentId + " не найден"));
        if (!oldComment.getAuthor().equals(user))
            throw new WrongRequestException("User is not comment author");
        Comment updatedComment = CommentMapper.updatedComment(commentDto, oldComment);
        return CommentMapper.toCommentDto(commentRepository.save(updatedComment));
    }

    @Override
    public CommentDto getCommentByAdmin(Long commentId) {
        return CommentMapper.toCommentDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id: " + commentId + " не найден")));
    }

    @Override
    public CommentDto getCommentByAuthor(Long userId, Long commentId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        return CommentMapper.toCommentDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id: " + commentId + " не найден")));
    }

    @Override
    public List<CommentDto> getAllComments(Long eventId, Integer from, Integer size) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id: " + eventId + " не найдено"));
        Pageable pageable = PageRequest.of(from / size, size);
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment : commentRepository
                .getAllByEventId(eventId, pageable)) {
            CommentDto commentDto = CommentMapper.toCommentDto(comment);
            commentDtoList.add(commentDto);
        }
        return commentDtoList;
    }

    @Override
    public List<CommentDto> getAllCommentsByAuthor(Long authorId, Integer from, Integer size) {
        userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + authorId + " не найден"));
        Pageable pageable = PageRequest.of(from / size, size);
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment : commentRepository
                .getAllByAuthorId(authorId, pageable)) {
            CommentDto commentDto = CommentMapper.toCommentDto(comment);
            commentDtoList.add(commentDto);
        }
        return commentDtoList;
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id: " + commentId + " не найден"));
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteCommentByAuthor(Long userId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id: " + commentId + " не найден"));
        if (!comment.getAuthor().equals(user))
            throw new WrongRequestException("Пользователь не является автором комментария");
        commentRepository.deleteById(commentId);
    }
}
