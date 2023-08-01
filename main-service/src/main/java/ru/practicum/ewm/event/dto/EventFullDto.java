package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * EventFullDto
 */
@Data
public class EventFullDto   {
  Long id;
  String annotation;
  CategoryDto category;
  Long confirmedRequests;
  LocalDateTime createdOn;
  String description;
  UserDto initiator;
  Location location;
  String title;
  EventState state;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime eventDate;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime publishedOn;
  Boolean paid;
  Boolean requestModeration;
  Long participantLimit;
  Long views;
}
