package ru.practicum.view.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.view.dto.NewUserRequest;
import ru.practicum.view.dto.UserDto;
import ru.practicum.view.service.UserService;

import java.util.List;

@PageTitle("Работа с пользователями")
@Route(value = "/users")
public class UserView extends VerticalLayout {

    private Comp comp;
    private final UserService service;
    private Grid<UserDto> usersGrid;
    private AddUserDialog addUserDialog;

    ValidationMessage firstNameValidationMessage = new ValidationMessage();
    ValidationMessage emailValidationMessage = new ValidationMessage();

    @Autowired
    public UserView(Comp comp, UserService userService) {

        this.service = userService;

        Tabs mainTabs = comp.getMainTabs();
        mainTabs.setSelectedIndex(1);
        setHorizontalComponentAlignment(Alignment.CENTER, mainTabs);

        add(mainTabs);

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("60%");
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout buttonLayout = new HorizontalLayout();

        Button createButton = new Button("Создать", new Icon(VaadinIcon.PLUS));
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        createButton.addClickListener(clickEvent -> {
            addUserDialog = new AddUserDialog();
            addUserDialog.addListener(SaveEvent.class, this::saveUser);
        });

        setHorizontalComponentAlignment(Alignment.CENTER, layout);

        Button refreshButton = new Button("Обновить", new Icon(VaadinIcon.REFRESH));
        refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        refreshButton.addClickListener(clickEvent -> {
            //  UI.getCurrent().getPage().reload();
            this.refreshGrid(usersGrid);
        });

        setHorizontalComponentAlignment(Alignment.END, buttonLayout);

        usersGrid = getUsersGrid();

        List<UserDto> users = getUsers(null, 0, 10);

        GridListDataView<UserDto> dataView = usersGrid.setItems(users);
        TextField searchField = new TextField();
        //  searchField.setWidth("40px");
        searchField.setPlaceholder("Поиск");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(person -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesFullName = matchesTerm(person.getName(),
                    searchTerm);
            boolean matchesEmail = matchesTerm(person.getEmail(), searchTerm);

            return matchesFullName || matchesEmail;
        });

        setHorizontalComponentAlignment(Alignment.START, searchField);
        buttonLayout.add(searchField, createButton, refreshButton);
        layout.add(buttonLayout);
        setHorizontalComponentAlignment(Alignment.CENTER, usersGrid);
        layout.add(usersGrid, firstNameValidationMessage, emailValidationMessage);

        setHorizontalComponentAlignment(Alignment.CENTER, layout);
        add(layout);
    }

    private Grid<UserDto> getUsersGrid() {

        Grid<UserDto> grid = new Grid<>(UserDto.class, false);
        Editor<UserDto> editor = grid.getEditor();

        grid.addColumn(UserDto::getId).setHeader("ID").setSortable(true).setWidth("30px").setKey("id");
        grid.addColumn(UserDto::getName).setHeader("Имя").setSortable(true).setKey("name");
        grid.addColumn(UserDto::getEmail).setHeader("E-mail").setSortable(true).setKey("email");

        Grid.Column<UserDto> editColumn = grid.addComponentColumn(userDto -> {
            Button editButton = new Button("Редактировать");
            editButton.setIcon(new Icon(VaadinIcon.EDIT));
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                grid.getEditor().editItem(userDto);
            });
            return editButton;
        }).setHeader("Действия").setWidth("200px").setFlexGrow(0);

        Binder<UserDto> binder = new Binder<>(UserDto.class);
        editor.setBinder(binder);
        editor.setBuffered(true);

        editor.addSaveListener(event -> {
            UserDto userDto = event.getItem();

            try {
                service.update(userDto, userDto.getId());
                Notification notification = Notification
                        .show("Пользователь успешно отредактирован");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setPosition(Notification.Position.BOTTOM_START);
            } catch (RetryableException ex) {
                String text;
                if (409 == ex.status()) {
                    text = "Пользователь с таким E-mail уже существует!";
                } else {
                    text = "Пользователя не удалось отредактировать!";
                }
                Notification notification = Notification
                        .show(text);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setPosition(Notification.Position.BOTTOM_START);
                userDto = service.getUserById(event.getItem().getId());
                event.getItem().setEmail(userDto.getEmail());
                editor.editItem(event.getItem());
            }
            editor.refresh();
        });

        editor.addCancelListener(event -> {
            firstNameValidationMessage.setText("");
            emailValidationMessage.setText("");
        });

        TextField firstNameField = new TextField();
        firstNameField.setWidthFull();

        binder.forField(firstNameField)
                .asRequired("Имя не должно быть пустым")
                .withStatusLabel(firstNameValidationMessage)
                .bind(UserDto::getName, UserDto::setName);
        grid.getColumnByKey("name").setEditorComponent(firstNameField);

        EmailField emailField = new EmailField();
        emailField.setWidthFull();
        binder.forField(emailField).asRequired("E-mail не должен быть пустым")
                .withValidator(new EmailValidator(
                        "Введите корректный e-mail"))
                .withStatusLabel(emailValidationMessage)
                .bind(UserDto::getEmail, UserDto::setEmail);
        grid.getColumnByKey("email").setEditorComponent(emailField);

        // чтобы кнопки "Сохранить" и "Отменить" срабатывали, не нужно использовать
        // @Data в Lombok (https://vaadin.com/forum/thread/18453090/grid-with-editor-can-t-save-changed-data)

        Button saveButton = new Button("Сохранить", e -> editor.save());
        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> {
                    UserDto userDto = service.getUserById(editor.getItem().getId());
                    editor.getItem().setName(userDto.getName());
                    editor.getItem().setEmail(userDto.getEmail());
                    editor.cancel();
                });
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(saveButton,
                cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, UserDto) -> {
                    button.addThemeVariants(
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> this.deleteUser(UserDto));
                    button.setText("Удалить");
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                }));

        grid.setAllRowsVisible(true);
        return grid;
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private void deleteUser(UserDto userDto) {
        if (userDto == null)
            return;
        service.delete(userDto.getId());
        refreshGrid(usersGrid);
        Notification notification = Notification
                .show("Пользователь удален!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private List<UserDto> getUsers(Long[] ids, Integer from, Integer size) {
        return service.getUsers(null, 0, 10);
    }

    private void refreshGrid(Grid<UserDto> usersGrid) {
        List<UserDto> users = getUsers(null, 0, 10);
        usersGrid.setItems(users);
        usersGrid.setAllRowsVisible(true);
        if (users.size() > 0) {
            usersGrid.setVisible(true);
            usersGrid.getDataProvider().refreshAll();
        } else {
            usersGrid.setVisible(false);
        }
    }

    private void saveUser(SaveEvent saveEvent) {
        try {
            service.create(new NewUserRequest(saveEvent.getUserDto().getEmail(),
                    saveEvent.getUserDto().getName()));
            Notification notification = Notification
                    .show("Пользователь успешно добавлен");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.BOTTOM_START);
        } catch (RetryableException ex) {
            String text;
            if (409 == ex.status()) {
                text = "Пользователь с таким E-mail уже существует!";
            } else {
                text = "Пользователя не удалось добавить!";
            }
            Notification notification = Notification
                    .show(text);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.BOTTOM_START);
        }
        refreshGrid(usersGrid);
    }
}