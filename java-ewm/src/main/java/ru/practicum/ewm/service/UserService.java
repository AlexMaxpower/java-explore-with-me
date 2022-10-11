package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.entity.User;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getUsers(Long[] ids, Integer from, Integer size);

    UserDto getUserById(Long id);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long id);

    void delete(Long userId);

    User getUser(Long userId);

}
