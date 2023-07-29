package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest user);

    Collection<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long id);
}
