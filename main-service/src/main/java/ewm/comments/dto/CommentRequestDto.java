package ewm.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    @Size(max = 1000)
    @NotBlank
    @NotNull
    private String text;
    private LocalDateTime created;
    private LocalDateTime edited;
    private Long confirmedRequests;
}
