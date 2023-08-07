package ru.practicum.ewm.comment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/comments")
public class PublicCommentController {
    private final CommentService commentService;

    public PublicCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@RequestBody @Valid CommentDto commentDto,
                                    @PathVariable Long userId,
                                    @PathVariable Long eventId) {
        return commentService.createComment(commentDto, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@RequestBody @Valid UpdateCommentDto updateCommentDto,
                                    @PathVariable Long userId,
                                    @PathVariable Long commentId) {
        return commentService.updateComment(updateCommentDto, userId, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAuthor(@PathVariable Long userId,
                                      @PathVariable Long commentId) {
        commentService.deleteCommentByAuthor(userId, commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllByAuthor(@PathVariable Long userId,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getAllCommentsByAuthor(userId, from, size);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentByAuthor(@PathVariable Long userId,
                                         @PathVariable Long commentId) {
        return commentService.getCommentByAuthor(userId, commentId);
    }
}
