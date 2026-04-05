package ui;

import model.Bill;
import service.BillingManager;
import service.FileManager;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class BillingPane extends BorderPane {

    private final BillingManager         billing;
    private final TableView<Bill>        table = new TableView<>();
    private final ObservableList<Bill>   data  = FXCollections.observableArrayList();
    private final TextArea               receiptArea = new TextArea();

    public BillingPane(BillingManager billing) {
        this.billing = billing;
        setPadding(new Insets(20));
        buildUI();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        Label heading = new Label("💳  Billing");
        heading.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#1a237e;");

        // ── Table ─────────────────────────────────────────────────────────
        TableColumn<Bill, Number> colId    = new TableColumn<>("Booking ID");
        TableColumn<Bill, String> colCust  = new TableColumn<>("Customer");
        TableColumn<Bill, Number> colRoom  = new TableColumn<>("Room");
        TableColumn<Bill, Number> colRoom2 = new TableColumn<>("Room Charge (₹)");
        TableColumn<Bill, Number> colSvc   = new TableColumn<>("Services (₹)");
        TableColumn<Bill, Number> colTotal = new TableColumn<>("Total (₹)");
        TableColumn<Bill, String> colPaid  = new TableColumn<>("Status");

        colId.setCellValueFactory(c    -> new SimpleIntegerProperty(c.getValue().getBookingId()));
        colCust.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().getCustomerName()));
        colRoom.setCellValueFactory(c  -> new SimpleIntegerProperty(c.getValue().getRoomNumber()));
        colRoom2.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getRoomCharge()));
        colSvc.setCellValueFactory(c   -> new SimpleDoubleProperty(c.getValue().getServiceCharge()));
        colTotal.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalAmount()));
        colPaid.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().isPaid() ? "✅ PAID" : "⏳ PENDING"));

        for (TableColumn<?,?> col : new TableColumn[]{colId,colCust,colRoom,colRoom2,colSvc,colTotal,colPaid})
            col.setStyle("-fx-alignment:CENTER;");

        table.getColumns().addAll(colId, colCust, colRoom, colRoom2, colSvc, colTotal, colPaid);
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size:13px;");

        // Click table row → show receipt
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) receiptArea.setText(sel.generateReceipt());
        });

        // ── Form ──────────────────────────────────────────────────────────
        TextField tfRoom = new TextField();
        tfRoom.setPromptText("Room Number");
        tfRoom.setPrefWidth(120);

        Button btnGenerate = styledBtn("🧾 Generate Bill",  "#1565c0");
        Button btnMarkPaid = styledBtn("✅ Mark Paid",       "#388e3c");
        Button btnExport   = styledBtn("📄 Export to File", "#6a1b9a");
        Button btnRefresh  = styledBtn("🔄 Refresh",        "#37474f");

        btnGenerate.setOnAction(e -> {
            try {
                int  rn   = Integer.parseInt(tfRoom.getText().trim());
                Bill bill = billing.generateBill(rn);
                if (bill != null) {
                    receiptArea.setText(bill.generateReceipt());
                    refresh();
                } else {
                    alert("No active booking found for Room " + rn + ".");
                }
            } catch (NumberFormatException ex) { alert("Enter a valid room number."); }
        });

        btnMarkPaid.setOnAction(e -> {
            Bill sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { alert("Select a bill from the table first."); return; }
            billing.markPaid(sel.getBookingId());
            refresh();
        });

        btnExport.setOnAction(e -> {
            Bill sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { alert("Select a bill from the table first."); return; }
            FileManager.exportBill(sel);          // Week 5 – FileWriter
            alert("Bill exported to data/bills.txt");
        });

        btnRefresh.setOnAction(e -> refresh());

        HBox form = new HBox(10, lbl("Room #:"), tfRoom, btnGenerate, btnMarkPaid, btnExport, btnRefresh);
        form.setPadding(new Insets(12, 0, 12, 0));

        // ── Receipt area ──────────────────────────────────────────────────
        receiptArea.setEditable(false);
        receiptArea.setPrefHeight(230);
        receiptArea.setStyle("-fx-font-family:monospace; -fx-font-size:12px;");

        VBox bottom = new VBox(6, lbl("Receipt Preview (click a row or generate):"), receiptArea);
        bottom.setPadding(new Insets(8, 0, 0, 0));

        setTop(new VBox(6, heading, new Separator(), form));
        setCenter(table);
        setBottom(bottom);
    }

    public void refresh() { data.setAll(billing.getAllBills()); }

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
