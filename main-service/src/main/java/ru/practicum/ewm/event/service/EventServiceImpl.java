package ru.practicum.ewm.event.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.WrongRequestException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository,
                            CategoryRepository categoryRepository, RequestRepository requestRepository, StatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
        this.statsClient = statsClient;
    }

    private static void updateEventStatusByUser(UpdateEventRequest eventUserRequest, Event event) {
        if (eventUserRequest.getStateAction() != null) {
            switch (eventUserRequest.getStateAction()) {
                case "CANCEL_REVIEW":
                    event.setState(EventState.CANCELED);
                    break;
                case "SEND_TO_REVIEW":
                    event.setState(EventState.PENDING);
                    break;
                default:
                    throw new ValidationException("Некорректное состояние события");
            }
        }
    }

    private static void updateEventStatusByAdmin(UpdateEventRequest eventAdminRequest, Event event) {
        if (eventAdminRequest.getStateAction() != null) {
            switch (eventAdminRequest.getStateAction()) {
                case "PUBLISH_EVENT":
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case "REJECT_EVENT":
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new ValidationException("Некорректное состояние события");
            }
        }
    }

    @Override
    public EventFullDto createEvent(NewEventDto newEventRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Event event = EventMapper.toEvent(newEventRequest);
        Category category = categoryRepository.findById(newEventRequest.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с id: " + newEventRequest.getCategory() + " не найдена"));
        if (event.getEventDate().minusHours(2).isBefore(LocalDateTime.now()))
            throw new WrongRequestException("Некорректная дата события");
        if (newEventRequest.getPaid() == null)
            event.setPaid(false);
        if (newEventRequest.getParticipantLimit() == null)
            event.setParticipantLimit(0L);
        if (newEventRequest.getRequestModeration() == null)
            event.setRequestModeration(true);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(user);
        event.setConfirmedRequests(0L);
        event.setState(EventState.PENDING);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEvent(Long id, Long userId) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
        return EventMapper.toEventFullDto(eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id: " + id + " не найдено")));
    }

    @Override
    public List<EventFullDto> getEventsByUser(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository
                .getByInitiatorId(userId, pageable)
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByInitiator(Long userId, Long eventId, UpdateEventRequest updateEventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id: " + eventId + " не найдено"));
        if (!user.getId().equals(event.getInitiator().getId()))
            throw new WrongRequestException(
                "Пользователь с id: " + user.getId() + " не является создателем события с id: " + event.getId());
        if (event.getState().equals(EventState.PUBLISHED))
            throw new ConflictException("Событие уже опубликовано");
        updateEventStatusByUser(updateEventDto, event);
        updateEventParams(updateEventDto, event);
        return EventMapper.toEventFullDto((eventRepository.save(event)));
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequest eventUpdateDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id: " + eventId + " не найдено"));
        if (event.getState().equals(EventState.PUBLISHED) || event.getState().equals(EventState.CANCELED))
            throw new ConflictException("Только ожидающие события могут быть обновлены");
        updateEventStatusByAdmin(eventUpdateDto, event);
        updateEventParams(eventUpdateDto, event);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    public List<EventFullDto> getEventsByAdmin(List<Long> users,
                                               List<EventState> states,
                                               List<Long> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Integer from,
                                               Integer size) {
        List<Event> events = eventRepository.getAllEventsByAdmin(users, states, categories, rangeStart, rangeEnd, PageRequest.of(from / size, size));
        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               String sort,
                                               Integer from,
                                               Integer size,
                                               HttpServletRequest request) {
        LocalDateTime now = LocalDateTime.now();
        if (rangeStart == null)
            rangeStart = now.minusYears(1);
        if (rangeEnd == null)
            rangeEnd = now.plusYears(1);
        if (rangeStart.isAfter(rangeEnd))
            throw new BadRequestException("Start is before end");
        String sorting;
        switch (sort) {
            case "EVENT_DATE":
                sorting = "eventDate";
                break;
            case "VIEWS":
                sorting = "views";
                break;
            default:
                sorting = "id";
                break;
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(sorting));
        List<Event> sortedEvents = eventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, pageable);
        if (onlyAvailable)
            sortedEvents.removeIf(event -> event.getParticipantLimit().equals(event.getConfirmedRequests()));
        if (sortedEvents.isEmpty())
            return List.of();
        String uri = request.getRequestURI();
        LocalDateTime startDate = sortedEvents.stream().map(Event::getCreatedOn).min(Comparator.naturalOrder()).orElse(now);
        long viewsBefore = getViewsFromStats(uri, startDate, now);
        sendStats(uri, request.getRemoteAddr(), "main");
        long viewsAfter = getViewsFromStats(uri, startDate, now);
        if (viewsBefore != viewsAfter)
            sortedEvents.forEach(e -> e.setViews(e.getViews() + 1));
        return eventRepository.saveAll(sortedEvents).stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventPublic(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id: " + eventId + " не найдено"));
        if (!event.getState().equals(EventState.PUBLISHED))
            throw new NotFoundException("Событие не опубликовано");
        String uri = request.getRequestURI();
        sendStats(uri, request.getRemoteAddr(), "main");
        long views = getViewsFromStats(uri, event.getCreatedOn(), LocalDateTime.now());
        if (event.getViews() != views)
            event.setViews(views);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<RequestDto> getEventRequests(Long userId, Long eventId) {
        return requestRepository
                .getAllByEventId(eventId)
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    private void updateEventParams(UpdateEventRequest updateEventDto, Event event) {
        if (updateEventDto.getCategory() != null)
            event.setCategory(categoryRepository.findById(updateEventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id: " + updateEventDto.getCategory() + " не найдена")));
        if (updateEventDto.getEventDate() != null) {
            if (updateEventDto.getEventDate().minusHours(2).isBefore(LocalDateTime.now()))
                throw new WrongRequestException("Date of event is NOT correct");
        }
        if (updateEventDto.getLocation() != null) {
            event.setLon(updateEventDto.getLocation().getLon());
            event.setLat(updateEventDto.getLocation().getLat());
        }
        Optional.ofNullable(updateEventDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventDto.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventDto.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updateEventDto.getTitle()).ifPresent(event::setTitle);
    }

    private long getViewsFromStats(String uri, LocalDateTime from, LocalDateTime to) {
        return Optional.ofNullable(statsClient.getStats(from, to, List.of(uri), true))
                .map(ResponseEntity::getBody)
                .stream()
                .flatMap(Collection::stream)
                .filter(v -> v.getUri().equals(uri))
                .mapToLong(ViewStats::getHits)
                .sum();
    }

    private void sendStats(String uri, String ip, String app) {
        EndpointHitDto hit = new EndpointHitDto();
        hit.setIp(ip);
        hit.setUri(uri);
        hit.setApp(app);
        hit.setTimestamp(LocalDateTime.now());
        statsClient.saveHit(hit);
    }
}
