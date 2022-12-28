package ru.practicum.view.service;

import org.springframework.stereotype.Service;
import ru.practicum.view.dto.NewUserRequest;
import ru.practicum.view.dto.UserDto;

import java.util.List;

@Service
public interface UserService {

    List<UserDto> getUsers(Long[] ids, Integer from, Integer size);
    void delete(Long userId);
    UserDto getUserById(Long id);
    UserDto update(UserDto userDto, Long id);
    UserDto create(NewUserRequest newUserRequest);
}
