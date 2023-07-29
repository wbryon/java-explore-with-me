package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Новое событие
 */
@Data
public class NewEventDto {
  @NotBlank
  @Length(min = 20, max = 2000)
  private String annotation;
  @NotNull
  private Long category;
  @NotBlank
  @Length(min = 20, max = 7000)
  private String description;
  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime eventDate;
  @NotNull
  private Location location;
  private Boolean paid;
  private Long participantLimit;
  private Boolean requestModeration;
  @NotBlank
  @Length(min = 3, max = 120)
  private String title;
}