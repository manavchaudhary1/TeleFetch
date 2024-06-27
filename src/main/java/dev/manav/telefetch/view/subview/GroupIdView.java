package dev.manav.telefetch.view.subview;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import dev.manav.telefetch.view.AppLayoutBasic;
import dev.manav.telefetch.model.GroupInfo;
import dev.manav.telefetch.service.GroupIdService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Telefetch-Groups")
@Route(value = "groupid", layout = AppLayoutBasic.class)
@SpringComponent
@UIScope
public class GroupIdView extends VerticalLayout {

    private final GroupIdService groupIdService;
    private VirtualList<GroupInfo> list;
    private List<GroupInfo> allChats;

    @Autowired
    public GroupIdView(GroupIdService groupIdService) {
        this.groupIdService = groupIdService;
        this.allChats = groupIdService.getMyChats();
        initView();
    }

    private void initView() {
        setWidth("50%");
        setHeightFull();
        getStyle().set("margin", "auto");

        H2 heading = new H2("Group ID Section");
        heading.getStyle()
                .set("color", "#ecf0f1")
                .set("text-align", "center")
                .set("margin-bottom", "20px")
                .set("font", "bold 24px/30px 'Open Sans', sans-serif");

        TextField searchField = new TextField();
        searchField.setPlaceholder("Search chats...");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> updateList(e.getValue()));

        list = new VirtualList<>();
        list.setItems(allChats);
        list.setRenderer(createGroupInfoRenderer());

        add(heading,searchField, list);
    }

    private void updateList(String filterText) {
        List<GroupInfo> filteredChats = allChats.stream()
                .filter(chat -> chat.getChatTitle().toLowerCase().contains(filterText.toLowerCase()))
                .collect(Collectors.toList());
        list.setItems(filteredChats);
    }

    private ComponentRenderer<Div, GroupInfo> createGroupInfoRenderer() {
        return new ComponentRenderer<>(groupInfo -> {
            Div card = new Div();
            card.getStyle()
                    .set("background-color", "#2c3e50")
                    .set("border-radius", "8px")
                    .set("box-shadow", "0 2px 4px rgba(0,0,0,.3)")
                    .set("padding", "16px")
                    .set("margin-bottom", "16px")
                    .set("width", "100%");

            H3 title = new H3(groupInfo.getChatTitle());
            title.getStyle()
                    .set("margin-top", "0")
                    .set("color", "#ecf0f1");

            Paragraph content = new Paragraph("Chat ID: " + groupInfo.getChatId());
            content.getStyle()
                    .set("color", "#bdc3c7");

            card.add(title, content);
            return card;
        });
    }
}