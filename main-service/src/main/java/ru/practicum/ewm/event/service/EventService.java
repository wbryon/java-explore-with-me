package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventRequest;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.request.dto.RequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(NewEventDto newEventRequest, Long userId);

    EventFullDto getEvent(Long id, Long userId);

    List<EventFullDto> getEventsByUser(Long userId, Integer from, Integer size);

    EventFullDto updateEventByInitiator(Long userId, Long eventId, UpdateEventRequest eventUpdateDto);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequest updateRequest);

    List<EventFullDto> getEventsByAdmin(List<Long> users,
                                        List<EventState> states,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Integer from,
                                        Integer size);

    List<EventShortDto> getEventsPublic(String text,
                                        List<Long> categories,
                                        Boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Boolean onlyAvailable,
                                        String sort,
                                        Integer from,
                                        Integer size,
                                        HttpServletRequest request
    );

    EventFullDto getEventPublic(Long id, HttpServletRequest request);

    List<RequestDto> getEventRequests(Long userId, Long eventId);
}
