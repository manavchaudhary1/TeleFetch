package dev.manav.telefetch.view.subview;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import dev.manav.telefetch.model.MessageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class GridLayout extends VerticalLayout {
    private final Grid<MessageInfo> grid;
    private final RestTemplate restTemplate;
    private boolean updatesActive;
    private final TextField searchField;

    public GridLayout(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        grid = new Grid<>(MessageInfo.class, false);
        grid.setAllRowsVisible(true);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(MessageInfo::getId).setHeader("ID").setWidth("100px").setFlexGrow(0);
        grid.addColumn(MessageInfo::getFilename).setHeader("Filename").setAutoWidth(true);
        grid.addColumn(MessageInfo::getSize).setHeader("Size in MB").setWidth("150px").setFlexGrow(0);
        grid.addColumn(MessageInfo::getCaption).setHeader("Caption").setFlexGrow(1);
        grid.addColumn(MessageInfo::getMimeType).setHeader("MIME Type").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::createProgressBar))
                .setHeader("Progress")
                .setFlexGrow(1)
                .setKey("progress")
                .setVisible(false);

        setWidthFull();
        setHeightFull();
        searchField = new TextField();
        searchField.setPlaceholder("Search messages");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> filterMessages());

        Button downloadButton = new Button("Download Selected Files", new Icon(VaadinIcon.DOWNLOAD), event -> {
            downloadSelectedFiles();
        });

        HorizontalLayout toolbar = new HorizontalLayout(searchField, downloadButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(Alignment.BASELINE);

        setWidthFull();
        setHeightFull();
        add(toolbar);
        add(grid);
        setVisible(false);
    }

    private void filterMessages() {
        String searchTerm = searchField.getValue().trim().toLowerCase();
        ListDataProvider<MessageInfo> dataProvider = (ListDataProvider<MessageInfo>) grid.getDataProvider();

        dataProvider.setFilter(message -> {
            if (searchTerm.isEmpty()) {
                return true;
            }

            return (message.getFilename() != null && message.getFilename().toLowerCase().contains(searchTerm)) ||
                    (message.getCaption() != null && message.getCaption().toLowerCase().contains(searchTerm));
        });
    }

    public void setItems(List<MessageInfo> items) {
        grid.setItems(items);
        setVisible(true);
    }

    private Component createProgressBar(MessageInfo messageInfo) {
        ProgressBar progressBar = new ProgressBar();
        progressBar.setWidth("100%");
        double progress = messageInfo.getProgress() / 100.0;
        progressBar.setValue(progress);

        if (progress >= 1.0) {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
        } else {
            progressBar.removeThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
        }

        return progressBar;
    }

    private void updateProgress() {
        String baseUrl = "http://localhost:8080/api/download/progress/";
        Set<MessageInfo> selectedFiles = new HashSet<>(grid.getSelectedItems());
        AtomicBoolean allCompleted = new AtomicBoolean(true);

        for (MessageInfo file : selectedFiles) {
            String url = baseUrl + file.getId();
            try {
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> fileInfo = response.getBody();
                    if (fileInfo != null && fileInfo.containsKey("progress")) {
                        double progress = ((Number) fileInfo.get("progress")).doubleValue();
                        file.setProgress(progress);
                        if (progress < 100) {
                            allCompleted.set(false);
                        }
                    }
                } else {
                    log.error("Failed to fetch progress for file ID: " + file.getId());
                    allCompleted.set(false);
                }
            } catch (RestClientException ex) {
                log.error("Error fetching progress for file ID: " + file.getId() + ". Error: " + ex.getMessage());
                allCompleted.set(false);
            }
        }

        getUI().ifPresent(ui -> ui.access(() -> {
            grid.getDataProvider().refreshAll();

            if (allCompleted.get()) {
                grid.deselectAll(); // Remove selection when all downloads are complete
                stopProgressUpdater();
                Notification notification = Notification.show("All downloads completed", 3000, Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        }));
    }

    private void startProgressUpdater() {
        if (!updatesActive) {
            updatesActive = true;
            getUI().ifPresent(ui -> {
                ui.setPollInterval(1000);
                ui.addPollListener(pollEvent -> {
                    if (updatesActive) {
                        updateProgress();
                    } else {
                        ui.setPollInterval(-1);
                    }
                });
            });
        }
    }

    private void stopProgressUpdater() {
        updatesActive = false;
        grid.getColumnByKey("progress").setVisible(false);
    }

    private void downloadSelectedFiles() {
        Set<MessageInfo> selectedFiles = grid.getSelectedItems();
        if (selectedFiles.isEmpty()) {
            Notification notification = Notification.show("No files selected");
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        String baseUrl = "http://localhost:8080/api/download/";
        for (MessageInfo file : selectedFiles) {
            String url = baseUrl + file.getId();
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    Notification.show("Download started for file: " + file.getFilename());
                    file.setProgress(0); // Reset progress when starting download
                } else {
                    Notification notification = Notification.show("Failed to start download for file: " + file.getFilename());
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } catch (RestClientException ex) {
                ex.printStackTrace();
                Notification.show("Failed to start download for file: " + file.getFilename());
            }
        }

        grid.getDataProvider().refreshAll();
        grid.getColumnByKey("progress").setVisible(true);
        startProgressUpdater();
    }
}