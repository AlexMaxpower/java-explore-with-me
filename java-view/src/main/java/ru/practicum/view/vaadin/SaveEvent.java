package ru.practicum.view.vaadin;

import ru.practicum.view.dto.UserDto;

public class SaveEvent extends UserDialogEvent {
    public SaveEvent(AddUserDialog source, UserDto userDto) {
        super(source, userDto);
    }
}
