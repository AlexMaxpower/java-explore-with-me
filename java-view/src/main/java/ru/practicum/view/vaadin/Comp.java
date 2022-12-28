package ru.practicum.view.vaadin;

import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouterLink;
import org.springframework.stereotype.Component;

@Component
public class Comp {

    public Tabs getMainTabs() {

        RouterLink mainLink = new RouterLink(MainView.class);
        mainLink.add("Главная");
        Tab mainTab = new Tab();
        mainTab.add(mainLink);

        RouterLink usersLink = new RouterLink(UserView.class);
        usersLink.add("Пользователи");
        Tab usersTab = new Tab();
        usersTab.add(usersLink);

        Tab dashboards = new Tab("Подборки");
        Tab documents = new Tab("События");
        Tab orders = new Tab("Категории");
        Tab products = new Tab("Комментарии");

        Tabs mainTabs = new Tabs(mainTab, usersTab, dashboards, documents,
                orders, products);
        mainTabs.addThemeVariants(TabsVariant.LUMO_HIDE_SCROLL_BUTTONS);
        mainTabs.addThemeVariants(TabsVariant.LUMO_CENTERED);

        return mainTabs;
    }

}
