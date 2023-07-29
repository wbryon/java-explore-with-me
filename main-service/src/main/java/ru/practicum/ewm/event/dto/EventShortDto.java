package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

/**
 * Краткая информация о событии
 */
@Data
public class EventShortDto {
  private String title;
  private Long id;
  private String annotation;
  private CategoryDto category;
  private Long confirmedRequests;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime eventDate;
  private UserShortDto initiator;
  private boolean paid;
  private Long views;
}
