package ru.practicum.view.vaadin;

import com.vaadin.flow.component.ComponentEvent;
import ru.practicum.view.dto.UserDto;

public abstract class UserDialogEvent extends ComponentEvent<AddUserDialog> {
    private UserDto userDto;

    protected UserDialogEvent(AddUserDialog source, UserDto userDto) {
        super(source, false);
        this.userDto = userDto;
    }

    public UserDto getUserDto() {
        return userDto;
    }
}
