package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class MainDashboard extends BorderPane {

    private final AppController controller;

    public MainDashboard(AppController controller) {
        this.controller = controller;
        buildUI();
    }

    private void buildUI() {
        // ── Header ────────────────────────────────────────────────────────
        Label title = new Label("🏨   Grand Hotel Management System");
        title.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:white;");
        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setStyle("-fx-background-color:#1a237e;");
        setTop(header);

        // ── Sidebar nav ───────────────────────────────────────────────────
        String base = "-fx-font-size:14px; -fx-pref-width:210px; -fx-pref-height:44px; "
                + "-fx-background-radius:8; -fx-cursor:hand; -fx-font-weight:bold;";
        String normal  = base + "-fx-background-color:#3949ab; -fx-text-fill:white;";
        String hover   = base + "-fx-background-color:#5c6bc0; -fx-text-fill:white;";

        Button btnRooms     = navBtn("🛏   Rooms",     normal, hover);
        Button btnBookings  = navBtn("📋   Bookings",  normal, hover);
        Button btnCustomers = navBtn("👤   Customers", normal, hover);
        Button btnBilling   = navBtn("💳   Billing",   normal, hover);

        btnRooms.setOnAction(e    -> controller.showPane("rooms"));
        btnBookings.setOnAction(e -> controller.showPane("bookings"));
        btnCustomers.setOnAction(e-> controller.showPane("customers"));
        btnBilling.setOnAction(e  -> controller.showPane("billing"));

        VBox nav = new VBox(12, btnRooms, btnBookings, btnCustomers, btnBilling);
        nav.setPadding(new Insets(24, 16, 24, 16));
        nav.setStyle("-fx-background-color:#1e2d7d;");
        setLeft(nav);

        // ── Welcome center ────────────────────────────────────────────────
        Label welcome = new Label("👈  Select a section from the sidebar");
        welcome.setStyle("-fx-font-size:17px; -fx-text-fill:#777;");
        StackPane center = new StackPane(welcome);
        center.setStyle("-fx-background-color:#f3f4f8;");
        setCenter(center);
    }

    private Button navBtn(String text, String style, String hoverStyle) {
        Button b = new Button(text);
        b.setStyle(style);
        b.setOnMouseEntered(e -> b.setStyle(hoverStyle));
        b.setOnMouseExited(e  -> b.setStyle(style));
        return b;
    }
}
