package ru.practicum.ewm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.AlreadyExistsException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.other.Pager;
import ru.practicum.ewm.service.UserService;
import ru.practicum.ewm.storage.UserRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, Pager {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.repository = userRepository;
        this.mapper = userMapper;
    }

    @Override
    public Collection<UserDto> getUsers(Long[] ids, Integer from, Integer size) {
        Pageable page = getPage(from, size, "id", Sort.Direction.ASC);
        List<User> users;
        if (ids != null) {
            users = repository.findByIdIsIn(Arrays.asList(ids), page);
        } else {
            users = repository.findAll(page).stream()
                    .collect(toList());
        }
        return users.stream()
                .map(mapper::userToUserDto)
                .collect(toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return mapper.userToUserDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found.")));
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        try {
            return mapper.userToUserDto(repository.save(mapper.userDtoToUser(userDto)));
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException("Пользователь с E-mail=" +
                    userDto.getEmail() + " уже существует!");
        }
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long id) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found."));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if ((userDto.getEmail() != null) && (userDto.getEmail() != user.getEmail())) {
            if (repository.findByEmail(userDto.getEmail())
                    .stream()
                    .filter(u -> u.getEmail().equals(userDto.getEmail()))
                    .allMatch(u -> u.getId().equals(userDto.getId()))) {
                user.setEmail(userDto.getEmail());
            } else {
                throw new AlreadyExistsException("Пользователь с E-mail=" + userDto.getEmail() + " уже существует!");
            }

        }
        return mapper.userToUserDto(repository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        try {
            repository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("User with id=" + userId + " not found.");
        }
    }

    @Override
    public User getUser(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found."));
    }
}