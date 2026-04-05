package app;

import service.BillingManager;
import service.FileManager;
import service.HotelManager;
import ui.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HotelManagementApp extends Application implements AppController {

    private HotelManager   hotelManager;
    private BillingManager billingManager;
    private MainDashboard  dashboard;
    private RoomPane       roomPane;
    private BookingPane    bookingPane;
    private CustomerPane   customerPane;
    private BillingPane    billingPane;
    private BorderPane     root;

    @Override
    public void start(Stage stage) {
        hotelManager   = new HotelManager();
        billingManager = new BillingManager(hotelManager);

        // Clean up old .ser files from previous version so they don't conflict
        FileManager.cleanLegacyFiles();

        // Load ALL persisted data
        hotelManager.loadAll();
        billingManager.loadBills();

        dashboard    = new MainDashboard(this);
        roomPane     = new RoomPane(hotelManager);
        bookingPane  = new BookingPane(hotelManager);
        customerPane = new CustomerPane(hotelManager);
        billingPane  = new BillingPane(billingManager);

        root = dashboard;

        Scene scene = new Scene(root, 1150, 700);
        stage.setTitle("Grand Hotel Management System");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(550);

        // Save ALL data on close
        stage.setOnCloseRequest(e -> {
            hotelManager.saveAll();
            billingManager.saveBills();
            System.out.println("[App] All data saved.");
        });

        stage.show();
    }

    @Override
    public void showPane(String paneName) {
        switch (paneName) {
            case "rooms"     -> { roomPane.refresh();     dashboard.setCenter(roomPane); }
            case "bookings"  -> { bookingPane.refresh();  dashboard.setCenter(bookingPane); }
            case "customers" -> { customerPane.refresh(); dashboard.setCenter(customerPane); }
            case "billing"   -> { billingPane.refresh();  dashboard.setCenter(billingPane); }
        }
    }

    public static void main(String[] args) { launch(args); }
}
