package ru.practicum.view.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String name;
}

