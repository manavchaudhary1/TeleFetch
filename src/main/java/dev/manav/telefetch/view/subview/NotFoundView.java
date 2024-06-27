package dev.manav.telefetch.view.subview;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import dev.manav.telefetch.view.AppLayoutBasic;
import jakarta.servlet.http.HttpServletResponse;

@PageTitle("Page Not Found")
@Route(value = "404", layout = AppLayoutBasic.class)
public class NotFoundView extends VerticalLayout implements HasErrorParameter<NotFoundException> {

    public NotFoundView() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(
                new H1("404"),
                new Paragraph("Oops! The page you're looking for doesn't exist."),
                new Paragraph("Please check the URL or navigate back to the home page.")
        );

        getStyle()
                .set("text-align", "center")
                .set("color", "#ecf0f1")
                .set("background-color", "#2c3e50");
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        return HttpServletResponse.SC_NOT_FOUND;
    }
}