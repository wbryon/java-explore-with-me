package ru.practicum.ewm.compilation.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Подборка событий
 */
@Data
public class NewCompilationDto {
    @NotBlank
    @Length(min = 1, max = 50)
    private String title;
    private boolean pinned;
    private List<Long> events;
}
