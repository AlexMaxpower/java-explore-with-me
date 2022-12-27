package ru.practicum.view.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class NewUserRequest {

    @Email
    @NotBlank
    private final String email;
    @NotBlank
    private final String name;

}
