package dev.manav.telefetch;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(variant = Lumo.DARK)
public class TeleFetchApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(TeleFetchApplication.class, args);
    }
}
