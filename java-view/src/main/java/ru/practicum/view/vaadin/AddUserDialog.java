package ru.practicum.view.vaadin;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import ru.practicum.view.dto.UserDto;


public class AddUserDialog extends Div {

    private TextField firstNameField = new TextField("Имя");
    private EmailField emailField = new EmailField("E-mail");

    public AddUserDialog() {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Новый пользователь");

        VerticalLayout dialogLayout = createDialogLayout();
        dialog.add(dialogLayout);

        Button saveButton = createSaveButton(dialog);
        Button cancelButton = new Button("Отмена", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        dialog.addDialogCloseActionListener(event -> {
        });

        dialog.open();

        getStyle().set("position", "fixed").set("top", "0").set("right", "0")
                .set("bottom", "0").set("left", "0").set("display", "flex")
                .set("align-items", "center").set("justify-content", "center");
    }

    private VerticalLayout createDialogLayout() {

        firstNameField.setMinLength(1);
        firstNameField.setPrefixComponent(VaadinIcon.USER.create());
        firstNameField.setRequired(true);
        firstNameField.setErrorMessage("Имя не должно быть пустым");
        emailField.setErrorMessage("Введите корректную электронную почту");
        emailField.setClearButtonVisible(true);
        emailField.setPrefixComponent(VaadinIcon.AT.create());

        VerticalLayout dialogLayout = new VerticalLayout(firstNameField,
                emailField);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private Button createSaveButton(Dialog dialog) {
        Button saveButton = new Button("Создать", e -> {
            if (firstNameField.getValue().isBlank()) {
                firstNameField.setInvalid(true);
            }
            if (emailField.getValue().isBlank()) {
                emailField.setInvalid(true);
            }
            if (!((firstNameField.isInvalid()) || (emailField.isInvalid()))) {

                UserDto userDto = new UserDto(null, emailField.getValue(), firstNameField.getValue());
                fireEvent(new SaveEvent(this, userDto));
                dialog.close();
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return saveButton;
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}



