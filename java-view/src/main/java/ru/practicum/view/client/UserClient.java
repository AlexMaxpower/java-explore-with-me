package ru.practicum.view.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.view.dto.NewUserRequest;
import ru.practicum.view.dto.UserDto;

import java.util.List;

@FeignClient(value = "userClient", url = "${feign.url}/admin/users")
public interface UserClient {

    @GetMapping("?ids={ids}&from={from}&size={size}")
    List<UserDto> getUsers(@PathVariable Long[] ids,
                           @PathVariable Integer from,
                           @PathVariable Integer size);

    @GetMapping("?from={from}&size={size}")
    List<UserDto> getAllUsers(@PathVariable Integer from,
                           @PathVariable Integer size);

    @DeleteMapping("/{userId}")
    void delete(@PathVariable Long userId);

    @GetMapping("/{userId}")
    UserDto getUserById(@PathVariable Long userId);

    @PutMapping ("/{userId}")
    UserDto update(@RequestBody UserDto userDto, @PathVariable Long userId);

    @PostMapping
    UserDto create(@RequestBody NewUserRequest newUserRequest);
}

