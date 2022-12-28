package ru.practicum.view.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.view.client.UserClient;
import ru.practicum.view.dto.NewUserRequest;
import ru.practicum.view.dto.UserDto;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserClient client;

    @Autowired
    public UserServiceImpl(UserClient userClient) {
        this.client = userClient;
    }

    @Override
    public List<UserDto> getUsers(Long[] ids, Integer from, Integer size) {
        if (ids != null) {
            return client.getUsers(ids, 0, 10);
        } else {
            return client.getAllUsers(0, 10);
        }
    }

    @Override
    public void delete(Long userId) {
        client.delete(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        List<UserDto> users = client.getUsers(new Long[] {userId}, 0, 10);
        return users.get(0);
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        return client.update(userDto, userId);
    }

    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        return client.create(newUserRequest);
    }
}
