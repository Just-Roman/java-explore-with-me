package ewm.comments.service;

import ewm.comments.dto.CommentDto;
import ewm.comments.dto.CommentRequestDto;

import java.util.List;

public interface CommentService {

    CommentDto create(long userId, long eventId, CommentRequestDto dto);

    CommentDto update(long userId, long eventId, long commentId, CommentRequestDto dto);

    void deleteCommentUser(long userId, long commentId);

    void deleteCommentAdmin(long commentId);

    CommentDto getCommentById(long commentId);

    List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size);

    List<CommentDto> getCommentsByUserId(Long userId, Integer from, Integer size);


}
