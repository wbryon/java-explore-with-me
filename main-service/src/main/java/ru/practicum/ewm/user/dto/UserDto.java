package ru.practicum.ewm.user.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Пользователь
 */
@Setter
@Getter
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
