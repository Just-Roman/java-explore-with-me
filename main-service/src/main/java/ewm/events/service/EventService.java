package ewm.events.service;

import ewm.events.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto create(long userId, EventCreateDto dto);

    EventFullDto updateEventByOwner(Long userId, Long eventId, EventUpdateUserDto dto);

    EventFullDto updateEventByAdmin(Long eventId, EventUpdateAdminDto dto);

    List<EventShortDto> getEventsByOwnerId(Long userId, Integer from, Integer size);

    EventFullDto getEventByOwner(Long userId, Long eventId);

    List<EventViewsFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                             Integer from, Integer size);

    List<EventViewsShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                       Integer size, HttpServletRequest request);

    EventViewsFullDto getEventById(Long eventId, HttpServletRequest request);


}
