package ui;

import model.*;
import service.HotelManager;
import threads.BookingTask;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class BookingPane extends BorderPane {

    private final HotelManager           manager;
    private final TableView<Booking>     table = new TableView<>();
    private final ObservableList<Booking> data = FXCollections.observableArrayList();
    private final TextArea               log   = new TextArea();

    public BookingPane(HotelManager manager) {
        this.manager = manager;
        setPadding(new Insets(20));
        buildUI();
        refresh();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        Label heading = new Label("📋  Bookings & Services");
        heading.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#1a237e;");

        // ── Table ─────────────────────────────────────────────────────────
        TableColumn<Booking, Number> colId     = new TableColumn<>("Booking ID");
        TableColumn<Booking, Number> colCust   = new TableColumn<>("Customer ID");
        TableColumn<Booking, Number> colRoom   = new TableColumn<>("Room");
        TableColumn<Booking, Number> colNights = new TableColumn<>("Nights");
        TableColumn<Booking, Number> colCost   = new TableColumn<>("Room Cost (₹)");
        TableColumn<Booking, String> colStatus = new TableColumn<>("Status");
        TableColumn<Booking, String> colIn     = new TableColumn<>("Check-In");

        colId.setCellValueFactory(c     -> new SimpleIntegerProperty(c.getValue().getBookingId()));
        colCust.setCellValueFactory(c   -> new SimpleIntegerProperty(c.getValue().getCustomerId()));
        colRoom.setCellValueFactory(c   -> new SimpleIntegerProperty(c.getValue().getRoomNumber()));
        colNights.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNights()));
        colCost.setCellValueFactory(c   -> new SimpleDoubleProperty(c.getValue().getRoomCost()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));
        colIn.setCellValueFactory(c     -> new SimpleStringProperty(c.getValue().getCheckIn().toString()));

        for (TableColumn<?,?> col : new TableColumn[]{colId,colCust,colRoom,colNights,colCost,colStatus,colIn})
            col.setStyle("-fx-alignment:CENTER;");

        table.getColumns().addAll(colId, colCust, colRoom, colNights, colCost, colStatus, colIn);
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size:13px;");

        // ── Input fields ──────────────────────────────────────────────────
        TextField tfCustId  = field("Customer ID",   80);
        TextField tfRoomNo  = field("Room Number",   100);
        TextField tfNights  = field("Nights",        70);
        TextField tfItems   = field("Items (laundry)", 110);
        TextField tfHours   = field("Extra Hours",   90);

        // ── Buttons ───────────────────────────────────────────────────────
        Button btnBook     = styledBtn("📋 Book Room",        "#1565c0");
        Button btnCheckout = styledBtn("🚪 Checkout",         "#c62828");
        Button btnLaundry  = styledBtn("👕 Laundry",          "#6a1b9a");
        Button btnLate     = styledBtn("⏰ Late Checkout",     "#e65100");
        Button btnRefresh  = styledBtn("🔄 Refresh",          "#37474f");

        // ── Book room (uses BookingTask thread – Week 3 & 4) ──────────────
        btnBook.setOnAction(e -> {
            try {
                int cid    = Integer.parseInt(tfCustId.getText().trim());
                int rn     = Integer.parseInt(tfRoomNo.getText().trim());
                int nights = Integer.parseInt(tfNights.getText().trim());

                // Week 3 – Runnable thread; Week 4 – synchronized inside HotelManager
                Thread t = new Thread(new BookingTask(cid, rn, nights, manager), "BookingTask-" + rn);
                t.start();
                try { t.join(); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }

                appendLog("Booking request sent for Room " + rn + " (Customer " + cid + ", " + nights + " nights).");
                refresh();
            } catch (NumberFormatException ex) { alert("Enter valid numeric IDs / nights."); }
        });

        // ── Checkout ──────────────────────────────────────────────────────
        btnCheckout.setOnAction(e -> {
            try {
                int rn = Integer.parseInt(tfRoomNo.getText().trim());
                boolean ok = manager.checkout(rn);
                appendLog(ok
                        ? "✅ Checkout done for Room " + rn + ". Cleaning thread started."
                        : "❌ No active booking for Room " + rn);
                refresh();
            } catch (NumberFormatException ex) { alert("Enter a valid room number."); }
        });

        // ── Laundry (LaundryTask thread – Week 3) ────────────────────────
        btnLaundry.setOnAction(e -> {
            try {
                int rn    = Integer.parseInt(tfRoomNo.getText().trim());
                int items = Integer.parseInt(tfItems.getText().trim());
                LaundryService ls = manager.requestLaundry(rn, items);
                appendLog(ls != null
                        ? "👕 Laundry request #" + ls.getServiceId() + " submitted. Charge: ₹" + ls.calculateCharge()
                        : "❌ Room " + rn + " has no active booking.");
            } catch (NumberFormatException ex) { alert("Enter valid room number and item count."); }
        });

        // ── Late checkout ─────────────────────────────────────────────────
        btnLate.setOnAction(e -> {
            try {
                int rn    = Integer.parseInt(tfRoomNo.getText().trim());
                int hours = Integer.parseInt(tfHours.getText().trim());
                LateCheckoutService lcs = manager.addLateCheckout(rn, hours);
                appendLog(lcs != null
                        ? "⏰ Late checkout added for Room " + rn + ". Charge: ₹" + lcs.calculateCharge()
                        : "❌ Room " + rn + " has no active booking.");
            } catch (NumberFormatException ex) { alert("Enter valid room number and hours."); }
        });

        btnRefresh.setOnAction(e -> refresh());

        // ── Layout ────────────────────────────────────────────────────────
        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(8);
        form.setPadding(new Insets(10, 0, 10, 0));

        form.addRow(0,
                lbl("Customer ID:"), tfCustId,
                lbl("Room #:"),  tfRoomNo,
                lbl("Nights:"),  tfNights);
        form.addRow(1,
                lbl("Items:"), tfItems,
                lbl("Extra Hrs:"), tfHours);

        HBox btnRow = new HBox(10, btnBook, btnCheckout, btnLaundry, btnLate, btnRefresh);
        btnRow.setPadding(new Insets(4, 0, 4, 0));

        log.setEditable(false);
        log.setPrefHeight(100);
        log.setStyle("-fx-font-family:monospace; -fx-font-size:12px;");

        setTop(new VBox(6, heading, new Separator(), form, btnRow));
        setCenter(table);
        setBottom(new VBox(4, lbl("Activity Log:"), log));
    }

    public void refresh() { data.setAll(manager.getAllBookings()); }

    private void appendLog(String msg) {
        log.appendText(msg + "\n");
    }

    private TextField field(String prompt, double width) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(width);
        return tf;
    }

    private Label lbl(String text) { return new Label(text); }

    private Button styledBtn(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:" + color + "; -fx-text-fill:white; "
                + "-fx-font-weight:bold; -fx-background-radius:6; -fx-cursor:hand;");
        return b;
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
