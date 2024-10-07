package dev.manav.telefetch.view.subview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.manav.telefetch.model.ChatResponse;
import dev.manav.telefetch.view.AppLayoutBasic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.component.orderedlayout.FlexComponent;

@PageTitle("Telefetch-Downloads")
@Route(value = "download",layout = AppLayoutBasic.class)
public class DownloadView extends VerticalLayout {
    private final TextField channelIdField;
    private final TextField limitField;
    private final GridLayout messagesGrid;

    @Autowired
    private RestTemplate restTemplate;

    public DownloadView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        H2 heading = new H2("Download Section");
        heading.getStyle()
                .set("color", "#ecf0f1")
                .set("text-align", "center")
                .set("margin-bottom", "20px")
                .set("font", "bold 24px/30px 'Open Sans', sans-serif");

        channelIdField = new TextField("Channel ID");
        limitField = new TextField("Limit");
        Button retrieveButton = new Button("Retrieve Messages", event -> retrieveMessages());
        messagesGrid = new GridLayout(restTemplate);

        HorizontalLayout inputLayout = new HorizontalLayout(channelIdField, limitField, retrieveButton);
        inputLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        inputLayout.setPadding(true);

        add(heading, inputLayout, messagesGrid);
        setPadding(true);
        setSpacing(true);
        setAlignItems(FlexComponent.Alignment.CENTER);
    }

    private void retrieveMessages() {
        String channelId = channelIdField.getValue();
        String limit = limitField.getValue();

        if (channelId.isEmpty() || limit.isEmpty()) {
            Notification notification = Notification.show("Please fill in both fields");
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        String url = "http://localhost:8080/api/chat";
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("chatId", Long.parseLong(channelId.trim()));
            request.put("limit", Integer.parseInt(limit));

            ChatResponse response = restTemplate.postForObject(url, request, ChatResponse.class);
            if (response != null) {
                messagesGrid.setItems(response.getMessages());
                Notification notification = Notification
                        .show("Messages retrieved successfully");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                Notification notification = Notification.show("No messages found");
                notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            }
        } catch (Exception e) {
            Notification notification = Notification.show("Error retrieving messages: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
