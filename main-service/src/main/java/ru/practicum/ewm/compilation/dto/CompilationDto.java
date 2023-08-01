package ru.practicum.ewm.compilation.dto;

import lombok.Data;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

/**
 * Подборка событий
 */
@Data
public class CompilationDto {
  private Long id;
  private List<EventShortDto> events;
  private Boolean pinned;
  private String title;
}
