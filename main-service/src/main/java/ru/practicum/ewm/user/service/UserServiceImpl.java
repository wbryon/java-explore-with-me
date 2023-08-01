package ru.practicum.ewm.user.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        try {
            User user = userRepository.save(UserMapper.toUser(userDto));
            return UserMapper.toUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Пользователь с именем: " + userDto.getName() + " уже зарегистрирован");
        }
    }

    @Override
    public List<UserDto> getAllUsers(List<Long> userIds, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (userIds == null || userIds.isEmpty())
            return userRepository.findAll(pageable)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        return userRepository.getAllByIdInOrderByIdDesc(userIds, pageable)
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
