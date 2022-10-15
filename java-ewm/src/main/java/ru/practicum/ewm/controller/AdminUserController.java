package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.request.NewUserRequest;
import ru.practicum.ewm.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
public class AdminUserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public AdminUserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public Collection<UserDto> getUsers(@RequestParam Long[] ids,
                                        @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                        @Valid @Positive @RequestParam(defaultValue = "10") Integer size,
                                        HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на получение списка пользователей",
                request.getRemoteAddr(),
                request.getRequestURI());
        return userService.getUsers(ids, from, size);
    }

    @PostMapping()
    public UserDto create(@Valid @RequestBody NewUserRequest newUserRequest, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на добавление пользователя {}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                newUserRequest.toString());
        return userService.create(userMapper.newUserRequestToUserDto(newUserRequest));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на удаление пользователя с ID={}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                userId);
        userService.delete(userId);
    }
}
