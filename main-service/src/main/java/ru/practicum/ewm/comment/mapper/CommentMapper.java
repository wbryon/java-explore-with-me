package ru.practicum.ewm.comment.mapper;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setAuthor(comment.getAuthor().getName());
        commentDto.setText(comment.getText());
        commentDto.setCreatedOn(comment.getCreatedOn());
        commentDto.setEventId(comment.getEvent().getId());
        return commentDto;
    }

    public static Comment toComment(CommentDto commentDto, User author, Event event) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());
        return comment;
    }

    public static Comment updateComment(CommentDto commentDto, Comment oldComment) {
        Comment comment = new Comment();
        comment.setId(oldComment.getId());
        comment.setAuthor(oldComment.getAuthor());
        comment.setEvent(oldComment.getEvent());
        comment.setText(commentDto.getText());
        comment.setCreatedOn(LocalDateTime.now());
        return comment;
    }
}
