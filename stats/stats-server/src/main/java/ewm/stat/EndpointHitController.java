package ewm.stat;

import dto.CreateDto;
import dto.ViewStatsDto;
import ewm.stat.service.EndpointHitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class EndpointHitController {
    private final EndpointHitService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public void create(@RequestBody CreateDto createDto) {
        service.create(createDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        return service.getStats(start, end, uris, unique);

    }

}
