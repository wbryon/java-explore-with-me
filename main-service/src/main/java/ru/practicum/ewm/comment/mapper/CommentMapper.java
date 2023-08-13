package ru.practicum.ewm.comment.mapper;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        if (comment.getId() != null)
            commentDto.setId(comment.getId());
        if (comment.getAuthor() != null)
            commentDto.setAuthor(comment.getAuthor().getName());
        if (comment.getText() != null)
            commentDto.setText(comment.getText());
        if (comment.getCreatedOn() != null)
            commentDto.setCreatedOn(comment.getCreatedOn());
        if (comment.getEvent().getId() != null)
            commentDto.setEventId(comment.getEvent().getId());
        return commentDto;
    }

    public static Comment toComment(CommentDto commentDto, User author, Event event) {
        Comment comment = new Comment();
        if (commentDto.getText() != null)
            comment.setText(commentDto.getText());
        if (author != null)
            comment.setAuthor(author);
        if (comment.getEvent() != null)
            comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());
        return comment;
    }

    public static Comment updateComment(CommentDto commentDto, Comment oldComment) {
        Comment comment = new Comment();
        if (oldComment.getId() != null)
            comment.setId(oldComment.getId());
        if (oldComment.getAuthor() != null)
            comment.setAuthor(oldComment.getAuthor());
        if (oldComment.getEvent() != null)
            comment.setEvent(oldComment.getEvent());
        if (oldComment.getText() == null)
            comment.setText(commentDto.getText());
        comment.setCreatedOn(LocalDateTime.now());
        return comment;
    }
}
