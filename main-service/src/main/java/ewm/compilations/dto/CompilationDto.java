package ewm.compilations.dto;

import ewm.events.dto.EventShortDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private Long id;
    private List<EventShortDto> events;
    private Boolean pinned;
    private String title;

    public CompilationDto(Long id, boolean pinned, String title) {
        this.id = id;
        this.pinned = pinned;
        this.title = title;
    }
}
