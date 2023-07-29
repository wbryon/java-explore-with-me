package ru.practicum.ewm.user.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Пользователь (краткая информация)
 */
@Setter
@Getter
public class UserShortDto {
    private Long id;
    private String name;
}
