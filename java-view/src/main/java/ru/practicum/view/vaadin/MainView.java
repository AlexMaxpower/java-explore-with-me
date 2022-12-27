package ru.practicum.view.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Главная страница сервиса ExploreWithMe")
@Route(value = "/")
public class MainView extends VerticalLayout {

    private TextField name;
    private Button sayHello;

    private Comp comp;

    @Autowired
    public MainView(Comp comp) {

        Tabs mainTabs = comp.getMainTabs();
        mainTabs.setSelectedIndex(0);

        TextArea homeText = getHomeText();

        setHorizontalComponentAlignment(Alignment.CENTER, mainTabs, homeText);
        add(mainTabs, homeText);
    }

    private TextArea getHomeText() {
        String info = "Привет! Это главная страница администратора сервиса ExporeWithMe. " +
                "Все необходимые функции управления пользователями, событиями, категориями, " +
                "подборками и комментариями ты сможешь найти выбрав соответствующий раздел.";

        TextArea textArea = new TextArea();
        textArea.setWidth("60%");
   //     textArea.setLabel("Основная информация");
        textArea.setValue(info);
        textArea.setReadOnly(true);

        return textArea;
    }
}