package ru.practicum.ewm.event.mapper;

import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.mapper.UserMapper;

public class EventMapper {
    public static Event toEvent(NewEventDto eventDto) {
        Event event = new Event();
        event.setAnnotation(eventDto.getAnnotation());
        event.setDescription(eventDto.getDescription());
        event.setEventDate(eventDto.getEventDate());
        event.setPaid(eventDto.getPaid());
        event.setParticipantLimit(eventDto.getParticipantLimit());
        event.setRequestModeration(eventDto.getRequestModeration());
        event.setTitle(eventDto.getTitle());
        event.setLat(eventDto.getLocation().getLat());
        event.setLon(eventDto.getLocation().getLon());
        event.setState(EventState.PENDING);
        event.setCategory(event.getCategory());
        event.setLon(eventDto.getLocation().getLon());
        event.setLat(eventDto.getLocation().getLat());
        return event;
    }

    public static EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setInitiator(UserMapper.toUserDtoShort(event.getInitiator()));
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setState(event.getState());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventFullDto.setLocation(new Location(event.getLat(), event.getLon()));
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setViews(event.getViews());
        return eventFullDto;
    }

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(event.getId());
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setInitiator(UserMapper.toUserDtoShort(event.getInitiator()));
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setViews(event.getViews());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setPaid(event.getPaid());
        return eventShortDto;
    }
}