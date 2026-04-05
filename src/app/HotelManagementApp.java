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
    private MainDashboard dashboard;
    private RoomPane      roomPane;
    private BookingPane   bookingPane;
    private CustomerPane  customerPane;
    private BillingPane   billingPane;

    private BorderPane root;

    @Override
    public void start(Stage stage) {

        // ── Service layer ─────────────────────────────────────────────────
        hotelManager   = new HotelManager();
        billingManager = new BillingManager(hotelManager);

        // Week 6 – Deserialize persisted rooms on startup
        FileManager.loadRooms().forEach(hotelManager::addRoom);
        hotelManager.seedDefaultRooms();   // no-op if rooms already loaded

        // Week 6 – Deserialize persisted bookings
        FileManager.loadBookings().forEach(b -> {
            // Re-mark occupied rooms based on active bookings
            if (b.getStatus() == model.Booking.Status.ACTIVE) {
                model.Room r = hotelManager.getRoom(b.getRoomNumber());
                if (r != null) r.setAvailable(false);
            }
        });

        // ── UI panes ──────────────────────────────────────────────────────
        dashboard    = new MainDashboard(this);
        roomPane     = new RoomPane(hotelManager);
        bookingPane  = new BookingPane(hotelManager);
        customerPane = new CustomerPane(hotelManager);
        billingPane  = new BillingPane(billingManager);

        root = dashboard;

        // ── Scene ─────────────────────────────────────────────────────────
        Scene scene = new Scene(root, 1150, 700);
        stage.setTitle("🏨  Grand Hotel Management System");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(550);

        stage.setOnCloseRequest(e -> {
            FileManager.saveRooms(hotelManager.getAllRooms());
            FileManager.saveBookings(hotelManager.getAllBookings());
            System.out.println("[App] Data saved on exit.");
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

    public static void main(String[] args) {
        launch(args);
    }
}
