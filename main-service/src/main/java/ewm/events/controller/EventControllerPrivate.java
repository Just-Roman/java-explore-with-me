package ewm.events.controller;

import ewm.events.dto.EventCreateDto;
import ewm.events.dto.EventFullDto;
import ewm.events.dto.EventShortDto;
import ewm.events.dto.EventUpdateUserDto;
import ewm.events.service.EventService;
import ewm.requests.dto.EventRequestStatusUpdateRequest;
import ewm.requests.dto.EventRequestStatusUpdateResult;
import ewm.requests.dto.RequestDto;
import ewm.requests.service.RequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class EventControllerPrivate {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @RequestBody @Valid EventCreateDto dto) {
        return eventService.create(userId, dto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByOwner(@PathVariable Long userId,
                                           @PathVariable Long eventId,
                                           @RequestBody @Valid EventUpdateUserDto updateEvent) {
        return eventService.updateEventByOwner(userId, eventId, updateEvent);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsStatus(@PathVariable Long userId,
                                                               @PathVariable Long eventId,
                                                               @RequestBody EventRequestStatusUpdateRequest request) {
        return requestService.updateRequestsStatus(userId, eventId, request);
    }

    @GetMapping
    List<EventShortDto> getEventsByOwner(@PathVariable Long userId,
                                         @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return eventService.getEventsByOwnerId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByOwner(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEventByOwner(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestsByEventOwner(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getRequestsByEventOwner(userId, eventId);
    }
}
