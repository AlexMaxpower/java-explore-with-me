package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.request.NewUserRequest;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto newUserRequestToUserDto(NewUserRequest newUserRequest);

    User userDtoToUser(UserDto userDto);

    UserDto userToUserDto(User user);

    UserShortDto userDtoToUserShortDto(UserDto userDto);

}
