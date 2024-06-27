package dev.manav.telefetch.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import dev.manav.telefetch.view.subview.GroupIdView;
import dev.manav.telefetch.view.subview.DownloadView;

public class AppLayoutBasic extends AppLayout {



    public AppLayoutBasic() {
        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("Telefetch");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "var(--lumo-space-m)");

        HorizontalLayout header = new HorizontalLayout(toggle, title);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(false);
        header.setPadding(true);
        header.setWidthFull();


        addToNavbar(header);

        addToDrawer(createSideNav());
    }

    private SideNav createSideNav() {
        SideNav sideNav = new SideNav();
        sideNav.getElement().setAttribute("theme", "dark");
        sideNav.getElement().setAttribute("style", "width: 250px;");

        sideNav.addItem(new SideNavItem("Home", MainView.class));
        sideNav.addItem(new SideNavItem("GroupIds", GroupIdView.class));
        sideNav.addItem(new SideNavItem("Download", DownloadView.class));

        return sideNav;
    }
}
