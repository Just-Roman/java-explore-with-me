package ewm.events.service;

import client.StatsClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.EndpointHitDto;
import dto.ViewStatsDto;
import ewm.categories.Category;
import ewm.categories.service.CategoryService;
import ewm.events.Event;
import ewm.events.EventMapper;
import ewm.events.EventRepository;
import ewm.events.dto.*;
import ewm.events.enums.State;
import ewm.events.enums.StateActionAdmin;
import ewm.events.enums.StateActionPrivate;
import ewm.exception.BadRequestException;
import ewm.exception.ForbiddenException;
import ewm.exception.NotFoundException;
import ewm.locations.Location;
import ewm.locations.service.LocationService;
import ewm.requests.RequestRepository;
import ewm.requests.dto.ConfirmedRequestsDto;
import ewm.user.User;
import ewm.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ewm.events.enums.State.*;
import static ewm.events.enums.StateActionAdmin.PUBLISH_EVENT;
import static ewm.events.enums.StateActionAdmin.REJECT_EVENT;
import static ewm.events.enums.StateActionPrivate.CANCEL_REVIEW;
import static ewm.events.enums.StateActionPrivate.SEND_TO_REVIEW;
import static ewm.requests.RequestStatus.CONFIRMED;

@Transactional
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    @Value("${app}")
    String app;

    @Override
    public EventFullDto create(long userId, EventCreateDto dto) {
        validateEventTime(dto.getEventDate());
        User user = userService.getModelById(userId);
        Category category = categoryService.checkAndReturnCategory(dto.getCategory());
        Location location = locationService.getOrSave(dto.getLocation());

        Event event = eventMapper.eventCreateDtoToModel(dto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(PENDING);

        return eventMapper.modelToEventFullDto(eventRepository.save(event), 0L);
    }

    @Override
    public EventFullDto updateEventByOwner(Long userId, Long eventId, EventUpdateUserDto dto) {
        Event event = checkAndReturnEvent(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new BadRequestException("Event must not be published");
        }
        if (event.getState() == PUBLISHED) {
            throw new ForbiddenException("Cannot update the event because it's not in the right state: PUBLISHED");
        }
        String annotation = dto.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            event.setAnnotation(annotation);
        }
        if (dto.getCategory() != null) {
            event.setCategory(categoryService.checkAndReturnCategory(dto.getCategory()));
        }
        String description = dto.getDescription();
        if (description != null && !description.isBlank()) {
            event.setDescription(description);
        }
        LocalDateTime eventDate = dto.getEventDate();
        if (eventDate != null) {
            validateEventTime(eventDate);
            event.setEventDate(eventDate);
        }
        if (dto.getLocation() != null) {
            event.setLocation(locationService.getOrSave(dto.getLocation()));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        String title = dto.getTitle();
        if (title != null && !title.isBlank()) {
            event.setTitle(title);
        }
        if (dto.getStateAction() != null) {
            StateActionPrivate stateActionPrivate = StateActionPrivate.valueOf(dto.getStateAction());
            if (stateActionPrivate.equals(SEND_TO_REVIEW)) {
                event.setState(PENDING);
            } else if (stateActionPrivate.equals(CANCEL_REVIEW)) {
                event.setState(CANCELED);
            }
        }
        event = eventRepository.save(event);
        return eventMapper.modelToEventFullDto(event, requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, EventUpdateAdminDto dto) {
        Event event = checkAndReturnEvent(eventId);

        if (dto.getStateAction() != null) {
            StateActionAdmin stateAction = StateActionAdmin.valueOf(dto.getStateAction());
            if (!event.getState().equals(PENDING) && stateAction.equals(PUBLISH_EVENT)) {
                throw new ForbiddenException("Cannot publish the event because it's not in the right state: not PENDING");
            }
            if (event.getState().equals(PUBLISHED) && stateAction.equals(REJECT_EVENT)) {
                throw new ForbiddenException("Cannot reject the event because it's not in the right state: PUBLISHED");
            }
            if (stateAction.equals(PUBLISH_EVENT)) {
                event.setState(PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (stateAction.equals(REJECT_EVENT)) {
                event.setState(State.CANCELED);
            }
        }

        String annotation = dto.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            event.setAnnotation(annotation);
        }
        if (dto.getCategory() != null) {
            event.setCategory(categoryService.checkAndReturnCategory(dto.getCategory()));
        }
        String description = dto.getDescription();
        if (description != null && !description.isBlank()) {
            event.setDescription(description);
        }
        LocalDateTime eventDate = dto.getEventDate();
        if (eventDate != null) {
            validateEventTime(eventDate);
            event.setEventDate(eventDate);
        }
        if (dto.getLocation() != null) {
            event.setLocation(locationService.getOrSave(dto.getLocation()));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        String title = dto.getTitle();
        if (title != null && !title.isBlank()) {
            event.setTitle(title);
        }

        event = eventRepository.save(event);
        return eventMapper.modelToEventFullDto(event, requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByOwnerId(Long userId, Integer from, Integer size) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size));
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                .stream()
                .collect(Collectors.toMap(ConfirmedRequestsDto::getEvent, ConfirmedRequestsDto::getCount));
        return events.stream()
                .map(event -> eventMapper.toEventShortDto(event, confirmedRequests.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByOwner(Long userId, Long eventId) {
        Event event = checkAndReturnEvent(eventId);
        if (event.getInitiator().getId() == userId) {
            return eventMapper.modelToEventFullDto(event, requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
        } else {
            throw new NotFoundException("userId не верный");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventViewsFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                    Integer from, Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Incorrectly made request, Start is after End");
        }
        Specification<Event> specification = Specification.where(null);
        if (users != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(users));
        }
        if (states != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("state").as(String.class).in(states));
        }
        if (categories != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }
        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
        List<Event> events = eventRepository.findAll(specification, PageRequest.of(from / size, size)).getContent();
        List<EventViewsFullDto> result = new ArrayList<>();
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());
        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new NotFoundException("Start not found"));
        ResponseEntity<Object> response = statsClient.getStats(start, LocalDateTime.now(), uris, true);

        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED).stream()
                .collect(Collectors.toMap(ConfirmedRequestsDto::getEvent, ConfirmedRequestsDto::getCount));
        for (Event event : events) {
            ObjectMapper mapper = new ObjectMapper();
            List<ViewStatsDto> statsDto = mapper.convertValue(response.getBody(), new TypeReference<>() {
            });
            if (!statsDto.isEmpty()) {
                result.add(eventMapper.toEventFullDtoWithViews(event, statsDto.getFirst().getHits(),
                        confirmedRequests.getOrDefault(event.getId(), 0L)));
            } else {
                result.add(eventMapper.toEventFullDtoWithViews(event, 0L,
                        confirmedRequests.getOrDefault(event.getId(), 0L)));
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventViewsShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                              Integer size, HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Incorrectly made request, Start is after End");
        }
        Specification<Event> specification = Specification.where(null);
        if (text != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                    ));
        }
        if (categories != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }
        if (paid != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("paid"), paid));
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = Objects.requireNonNullElseGet(rangeStart, () -> now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd));
        }
        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), PUBLISHED));
        PageRequest pageRequest;
        if (sort.equals("EVENT_DATE")) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("eventDate"));
        } else if (sort.equals("VIEWS")) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("views").descending());
        } else {
            throw new ValidationException("Unknown sort: " + sort);
        }
        List<Event> events = eventRepository.findAll(specification, pageRequest).getContent();
        List<EventViewsShortDto> result = new ArrayList<>();
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());
        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new NotFoundException("Start not found"));
        ResponseEntity<Object> response = statsClient.getStats(start, LocalDateTime.now(), uris, true);
        System.out.println(response.getBody());
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                .stream()
                .collect(Collectors.toMap(ConfirmedRequestsDto::getEvent, ConfirmedRequestsDto::getCount));
        for (Event event : events) {
            ObjectMapper mapper = new ObjectMapper();
            List<ViewStatsDto> statsDto = mapper.convertValue(response.getBody(), new TypeReference<>() {
            });
            if (!statsDto.isEmpty()) {
                result.add(eventMapper.toEventShortDtoWithViews(event, statsDto.getFirst().getHits(),
                        confirmedRequests.getOrDefault(event.getId(), 0L)));
            } else {
                result.add(eventMapper.toEventShortDtoWithViews(event, 0L,
                        confirmedRequests.getOrDefault(event.getId(), 0L)));
            }
        }
        EndpointHitDto endpointHitDto = new EndpointHitDto(app, request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        statsClient.save(endpointHitDto);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public EventViewsFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = checkAndReturnEvent(eventId);
        if (event.getState() != PUBLISHED) {
            throw new NotFoundException("Event is not PUBLISHED");
        }
        ResponseEntity<Object> response = statsClient.getStats(event.getCreatedOn(), LocalDateTime.now(),
                List.of(request.getRequestURI()), true);
        ObjectMapper mapper = new ObjectMapper();
        List<ViewStatsDto> statsDto = mapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        EventViewsFullDto result;
        if (!statsDto.isEmpty()) {
            result = eventMapper.toEventFullDtoWithViews(event, statsDto.getFirst().getHits(),
                    requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
        } else {
            result = eventMapper.toEventFullDtoWithViews(event, 0L,
                    requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
        }
        EndpointHitDto endpointHitDto = new EndpointHitDto(app, request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        statsClient.save(endpointHitDto);
        return result;
    }

    private void validateEventTime(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException(" должно содержать дату, которая еще не наступила. Value: " + eventTime);
        }
    }

    private Event checkAndReturnEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
    }

}
