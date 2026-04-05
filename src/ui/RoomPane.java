package ui;

import model.*;
import service.FileManager;
import service.HotelManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class RoomPane extends BorderPane {

    private final HotelManager            manager;
    private final TableView<Room>         table = new TableView<>();
    private final ObservableList<Room>    data  = FXCollections.observableArrayList();

    public RoomPane(HotelManager manager) {
        this.manager = manager;
        setPadding(new Insets(20));
        buildUI();
        refresh();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        // ── Heading ───────────────────────────────────────────────────────
        Label heading = new Label("🛏  Room Management");
        heading.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#1a237e;");

        // ── Table ─────────────────────────────────────────────────────────
        TableColumn<Room, Number> colNum   = new TableColumn<>("Room No");
        TableColumn<Room, String> colType  = new TableColumn<>("Type");
        TableColumn<Room, Number> colPrice = new TableColumn<>("₹/Night");
        TableColumn<Room, String> colAvail = new TableColumn<>("Status");
        TableColumn<Room, String> colClean = new TableColumn<>("Cleaning");

        colNum.setCellValueFactory(c   -> new SimpleIntegerProperty(c.getValue().getRoomNumber()));
        colType.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().getRoomType().getDisplayName()));
        colPrice.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getRoomType().getPricePerNight()));
        colAvail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isAvailable() ? "✅ Available" : "🔴 Occupied"));
        colClean.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCleaningStatus()));

        for (TableColumn<?,?> col : new TableColumn[]{colNum, colType, colPrice, colAvail, colClean}) {
            col.setStyle("-fx-alignment:CENTER;");
        }

        table.getColumns().addAll(colNum, colType, colPrice, colAvail, colClean);
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size:13px;");

        // ── Add-room form ─────────────────────────────────────────────────
        TextField tfRoomNo = new TextField();
        tfRoomNo.setPromptText("Room Number");
        tfRoomNo.setPrefWidth(120);

        ComboBox<String> cbType = new ComboBox<>(FXCollections.observableArrayList(
                "Standard (No AC)", "Standard (AC)", "Deluxe", "Suite"));
        cbType.setPromptText("Room Type");

        Button btnAdd     = styledBtn("➕ Add Room",        "#388e3c");
        Button btnRefresh = styledBtn("🔄 Refresh",         "#1565c0");
        Button btnSave    = styledBtn("💾 Save to File",    "#6a1b9a");
        Button btnReadRAF = styledBtn("📖 Read RAF Record", "#e65100");

        btnAdd.setOnAction(e -> handleAddRoom(tfRoomNo, cbType));
        btnRefresh.setOnAction(e -> refresh());
        btnSave.setOnAction(e -> {
            FileManager.saveRooms(manager.getAllRooms());
            alert("Rooms serialized to rooms.dat");
        });
        btnReadRAF.setOnAction(e -> {
            Room sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { alert("Select a room first."); return; }
            FileManager.readRoomRecord(sel.getRoomNumber());
            alert("RAF record printed to console for Room " + sel.getRoomNumber());
        });

        HBox form = new HBox(10,
                new Label("Room #:"), tfRoomNo,
                new Label("Type:"), cbType,
                btnAdd, btnRefresh, btnSave, btnReadRAF);
        form.setPadding(new Insets(12, 0, 12, 0));
        form.setStyle("-fx-alignment:center-left;");

        setTop(new VBox(6, heading, new Separator(), form));
        setCenter(table);
    }

    private void handleAddRoom(TextField tfRoomNo, ComboBox<String> cbType) {
        String numStr = tfRoomNo.getText().trim();
        String type   = cbType.getValue();
        if (numStr.isEmpty() || type == null) { alert("Please fill all fields."); return; }

        int rn;
        try { rn = Integer.parseInt(numStr); }
        catch (NumberFormatException ex) { alert("Room number must be numeric."); return; }

        Room r = switch (type) {
            case "Standard (No AC)" -> new StandardRoom(rn, false);
            case "Standard (AC)"    -> new StandardRoom(rn, true);
            case "Deluxe"           -> new DeluxeRoom(rn, true, false);
            default                 -> new DeluxeRoom(rn, true, true, true);
        };

        manager.addRoom(r);
        // Week 6 – write RAF record immediately
        FileManager.writeRoomRecord(r);
        refresh();
        tfRoomNo.clear();
        cbType.getSelectionModel().clearSelection();
    }

    public void refresh() {
        data.setAll(manager.getAllRooms());
    }

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
