package ui;

import model.Customer;
import service.HotelManager;
import util.ValidationUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CustomerPane extends BorderPane {

    private final HotelManager            manager;
    private final TableView<Customer>     table = new TableView<>();
    private final ObservableList<Customer> data = FXCollections.observableArrayList();

    public CustomerPane(HotelManager manager) {
        this.manager = manager;
        setPadding(new Insets(20));
        buildUI();
        refresh();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        Label heading = new Label("👤  Customer Management");
        heading.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#1a237e;");

        // ── Table ─────────────────────────────────────────────────────────
        TableColumn<Customer, Number> colId    = new TableColumn<>("ID");
        TableColumn<Customer, String> colName  = new TableColumn<>("Name");
        TableColumn<Customer, String> colPhone = new TableColumn<>("Phone");
        TableColumn<Customer, String> colEmail = new TableColumn<>("Email");
        TableColumn<Customer, String> colRoom  = new TableColumn<>("Room");

        colId.setCellValueFactory(c    -> new SimpleIntegerProperty(c.getValue().getCustomerId()));
        colName.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().getName()));
        colPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colRoom.setCellValueFactory(c  -> new SimpleStringProperty(
                c.getValue().getAllocatedRoom() == -1 ? "—" : String.valueOf(c.getValue().getAllocatedRoom())));

        for (TableColumn<?,?> col : new TableColumn[]{colId, colName, colPhone, colEmail, colRoom})
            col.setStyle("-fx-alignment:CENTER;");

        table.getColumns().addAll(colId, colName, colPhone, colEmail, colRoom);
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size:13px;");

        // ── Form ──────────────────────────────────────────────────────────
        TextField tfName  = new TextField(); tfName.setPromptText("Full Name");       tfName.setPrefWidth(160);
        TextField tfPhone = new TextField(); tfPhone.setPromptText("Phone (10 digits)"); tfPhone.setPrefWidth(140);
        TextField tfEmail = new TextField(); tfEmail.setPromptText("Email");           tfEmail.setPrefWidth(200);

        Button btnAdd     = styledBtn("➕ Add Customer", "#388e3c");
        Button btnRefresh = styledBtn("🔄 Refresh",      "#1565c0");

        btnAdd.setOnAction(e -> {
            String name  = tfName.getText().trim();
            String phone = tfPhone.getText().trim();
            String email = tfEmail.getText().trim();

            if (name.isBlank())                        { alert("Name is required.");            return; }
            if (!ValidationUtil.isValidPhone(phone))   { alert("Phone must be 10 digits.");     return; }
            if (!ValidationUtil.isValidEmail(email))   { alert("Invalid email address.");       return; }

            manager.addCustomer(name, phone, email);
            refresh();
            tfName.clear(); tfPhone.clear(); tfEmail.clear();
        });

        btnRefresh.setOnAction(e -> refresh());

        HBox form = new HBox(10,
                new Label("Name:"),  tfName,
                new Label("Phone:"), tfPhone,
                new Label("Email:"), tfEmail,
                btnAdd, btnRefresh);
        form.setPadding(new Insets(12, 0, 12, 0));

        setTop(new VBox(6, heading, new Separator(), form));
        setCenter(table);
    }

    public void refresh() { data.setAll(manager.getAllCustomers()); }

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
