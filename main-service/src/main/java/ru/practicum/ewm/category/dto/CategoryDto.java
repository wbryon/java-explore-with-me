package ru.practicum.ewm.category.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * Категория
 */
@Data
@NoArgsConstructor
public class CategoryDto {
  private Long id;
  @NotBlank
  @Length(max = 50)
  private String name;
}
