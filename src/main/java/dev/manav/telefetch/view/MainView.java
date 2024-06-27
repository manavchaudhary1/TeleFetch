package dev.manav.telefetch.view;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Telefetch")
@Route(value = "", layout = AppLayoutBasic.class)
public class MainView extends VerticalLayout {

    private final RestTemplate restTemplate;
    private Span welcomeMessage;

    public MainView(@Autowired RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        initView();
    }

    private void initView() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        this.welcomeMessage = new Span("Welcome, User!");
        this.welcomeMessage.getStyle()
                .set("font-size", "var(--lumo-font-size-xl)")
                .set("font-weight", "bold")
                .set("margin-bottom", "2rem");

        VerticalLayout content = new VerticalLayout(
                welcomeMessage,
                createProjectInfo()
        );
        content.setWidthFull();
        content.setMaxWidth("800px");
        content.setAlignItems(Alignment.CENTER);

        add(content);
        updateUsername();
    }

    private VerticalLayout createProjectInfo() {
        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setAlignItems(Alignment.CENTER);
        infoLayout.getStyle()
                .set("background-color", "#2c3e50")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("width", "100%");

        H2 aboutHeader = new H2("About TeleFetch");
        aboutHeader.getStyle().set("color", "#ecf0f1");

        Paragraph about = new Paragraph(
                "TeleFetch is a Spring Boot Project to download private group/channel messages of Telegram.\n It's an application for downloading documents from Telegram Private Channels that you are part of.\n Built with Spring Boot and using the Spring Boot Starter for Telegram, which is based on TDLib."
        );
        about.getStyle().set("color", "#bdc3c7")
                .set("white-space", "pre");

        Anchor githubLink = new Anchor("https://github.com/manavchaudhary1/TeleFetch", "GitHub Repository");
        githubLink.getStyle()
                .set("color", "#3498db")
                .set("text-decoration", "none")
                .set("font-weight", "bold");

        Paragraph issuesInfo = new Paragraph("For issues or feature requests, please use the GitHub Issues page.");
        issuesInfo.getStyle().set("color", "#bdc3c7");

        infoLayout.add(aboutHeader, about, githubLink, issuesInfo);
        return infoLayout;
    }

    private void updateUsername() {
        String url = "http://localhost:8080/api/user/active-usernames";
        try {
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode activeUsernamesNode = rootNode.get("activeUsernames");
            if (activeUsernamesNode != null && activeUsernamesNode.isArray() && activeUsernamesNode.size() > 0) {
                String username = activeUsernamesNode.get(0).asText();
                welcomeMessage.setText("Welcome, " + username + "!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}