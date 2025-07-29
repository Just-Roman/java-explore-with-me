package ewm.comments;

import ewm.comments.dto.CommentDto;
import ewm.comments.dto.CommentRequestDto;
import ewm.events.Event;
import ewm.events.EventMapper;
import ewm.user.User;
import ewm.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final UserMapper userMapper;
    private final EventMapper eventMapper;


    public Comment commentRequestDtoToModel(CommentRequestDto dto, User author, Event event) {
        return Comment.builder()
                .text(dto.getText())
                .author(author)
                .event(event)
                .created(dto.getCreated() != null ? dto.getCreated() : null)
                .edited(dto.getEdited() != null ? dto.getEdited() : null)
                .confirmedRequests(dto.getConfirmedRequests())
                .build();
    }

    public CommentDto modelToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(userMapper.modelToUserShortDto(comment.getAuthor()))
                .event(eventMapper.toEventShortDto(comment.getEvent(), comment.getConfirmedRequests()))
                .created(comment.getCreated())
                .edited(comment.getEdited() != null ? comment.getEdited() : null)
                .build();
    }

    public List<CommentDto> listModelToCommentDto(List<Comment> comments) {
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsDto.add(modelToCommentDto(comment));
        }
        return commentsDto;
    }

}
