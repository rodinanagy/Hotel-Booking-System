package com.hotel.ui;

import com.hotel.exception.InvalidDateException;
import com.hotel.exception.InvalidGuestCountException;
import com.hotel.exception.RoomNotAvailableException;
import com.hotel.model.*;
import com.hotel.service.BookingService;
import com.hotel.service.RoomAvailabilityTask;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.List;

public class MainController {
    private final BookingService bookingService = new BookingService();

    private DatePicker checkInPicker;
    private DatePicker checkOutPicker;
    private Spinner<Integer> guestSpinner;
    private TableView<Room> roomTable;
    private TextField nameField;
    private TextField emailField;
    private TextField phoneField;
    private Label statusLabel;
    private ProgressIndicator progressIndicator;
    private TableView<Booking> bookingTable;
    private Label bookingStatusLabel;

    public Parent buildUI() {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #1a1a2e;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #16213e;");
        Label title = new Label("HOTEL BOOKING SYSTEM");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.WHITE);
        header.getChildren().add(title);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab bookTab = new Tab("  Book a Room  ", buildBookTab());
        Tab myBookingsTab = new Tab("  My Bookings  ", buildMyBookingsTab());
        tabPane.getTabs().addAll(bookTab, myBookingsTab);

        VBox.setVgrow(tabPane, Priority.ALWAYS);
        root.getChildren().addAll(header, tabPane);
        return root;
    }

    private VBox buildBookTab() {
        VBox tab = new VBox(12);
        tab.setPadding(new Insets(20));
        tab.setStyle("-fx-background-color: #f0f4f8;");

        HBox filterRow = new HBox(12);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        filterRow.setPadding(new Insets(12));
        filterRow.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        checkInPicker = new DatePicker(LocalDate.now().plusDays(1));
        checkOutPicker = new DatePicker(LocalDate.now().plusDays(2));

        guestSpinner = new Spinner<>(1, 10, 1);
        guestSpinner.setPrefWidth(75);
        guestSpinner.setEditable(true);

        Button checkBtn = new Button("Check Availability");
        checkBtn.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 7 15;");
        checkBtn.setOnAction(e -> checkAvailability());

        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setPrefSize(28, 28);

        filterRow.getChildren().addAll(
            new Label("Check-in:"), checkInPicker,
            new Label("Check-out:"), checkOutPicker,
            new Label("Guests:"), guestSpinner,
            checkBtn, progressIndicator
        );

        statusLabel = new Label("Select dates and click 'Check Availability'");
        statusLabel.setStyle("-fx-text-fill: #666;");

        roomTable = buildRoomsTable();
        VBox.setVgrow(roomTable, Priority.ALWAYS);

        tab.getChildren().addAll(filterRow, statusLabel, roomTable, buildGuestForm());
        return tab;
    }

    @SuppressWarnings("unchecked")
    private TableView<Room> buildRoomsTable() {
        TableView<Room> table = new TableView<>();
        table.setPlaceholder(new Label("Click 'Check Availability' to see available rooms"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Room, Integer> numCol = new TableColumn<>("Room #");
        numCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getRoomNumber()));
        numCol.setMaxWidth(80);

        TableColumn<Room, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getRoomType()));

        TableColumn<Room, Integer> floorCol = new TableColumn<>("Floor");
        floorCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getFloor()));
        floorCol.setMaxWidth(65);

        TableColumn<Room, String> priceCol = new TableColumn<>("Price / Night");
        priceCol.setCellValueFactory(cd -> new SimpleStringProperty(
            String.format("%.2f LE", cd.getValue().getPricePerNight())));

        TableColumn<Room, Integer> guestsCol = new TableColumn<>("Max Guests");
        guestsCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getMaxGuests()));
        guestsCol.setMaxWidth(100);

        TableColumn<Room, String> amenitiesCol = new TableColumn<>("Amenities");
        amenitiesCol.setCellValueFactory(cd -> new SimpleStringProperty(
            String.join(", ", cd.getValue().getAmenities())));

        table.getColumns().addAll(numCol, typeCol, floorCol, priceCol, guestsCol, amenitiesCol);
        return table;
    }

    private TitledPane buildGuestForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setPrefWidth(200);

        emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.setPrefWidth(200);

        phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.setPrefWidth(150);

        Button bookBtn = new Button("Book Selected Room");
        bookBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 20;");
        bookBtn.setOnAction(e -> bookSelectedRoom());

        grid.addRow(0,
            new Label("Name:"), nameField,
            new Label("Email:"), emailField,
            new Label("Phone:"), phoneField);
        grid.add(bookBtn, 0, 1, 6, 1);

        TitledPane pane = new TitledPane("Guest Details", grid);
        pane.setCollapsible(false);
        return pane;
    }

    @SuppressWarnings("unchecked")
    private VBox buildMyBookingsTab() {
        VBox tab = new VBox(12);
        tab.setPadding(new Insets(20));
        tab.setStyle("-fx-background-color: #f0f4f8;");

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; " +
            "-fx-background-radius: 5; -fx-padding: 7 15;");
        refreshBtn.setOnAction(e -> loadBookings());

        Button cancelBtn = new Button("Cancel Selected Booking");
        cancelBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; " +
            "-fx-background-radius: 5; -fx-padding: 7 15;");
        cancelBtn.setOnAction(e -> cancelSelectedBooking());

        bookingStatusLabel = new Label();
        bookingStatusLabel.setStyle("-fx-text-fill: #28a745;");

        HBox btnRow = new HBox(10, refreshBtn, cancelBtn, bookingStatusLabel);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        bookingTable = buildBookingsTable();
        VBox.setVgrow(bookingTable, Priority.ALWAYS);

        tab.getChildren().addAll(btnRow, bookingTable);
        loadBookings();
        return tab;
    }

    @SuppressWarnings("unchecked")
    private TableView<Booking> buildBookingsTable() {
        TableView<Booking> table = new TableView<>();
        table.setPlaceholder(new Label("No bookings found"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Booking, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getId()));
        idCol.setMaxWidth(55);

        TableColumn<Booking, Integer> roomCol = new TableColumn<>("Room #");
        roomCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getRoom().getRoomNumber()));
        roomCol.setMaxWidth(75);

        TableColumn<Booking, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getRoom().getRoomType()));

        TableColumn<Booking, String> guestCol = new TableColumn<>("Guest");
        guestCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getGuest().getName()));

        TableColumn<Booking, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getGuest().getEmail()));

        TableColumn<Booking, String> checkInCol = new TableColumn<>("Check-in");
        checkInCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCheckIn().toString()));

        TableColumn<Booking, String> checkOutCol = new TableColumn<>("Check-out");
        checkOutCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCheckOut().toString()));

        TableColumn<Booking, Long> nightsCol = new TableColumn<>("Nights");
        nightsCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getNights()));
        nightsCol.setMaxWidth(65);

        TableColumn<Booking, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cd -> new SimpleStringProperty(
            String.format("%.2f LE", cd.getValue().getTotalCost())));

        TableColumn<Booking, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus()));
        statusCol.setCellFactory(col -> new TableCell<Booking, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item);
                    setStyle("ACTIVE".equals(item)
                        ? "-fx-text-fill: #28a745; -fx-font-weight: bold;"
                        : "-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                }
            }
        });

        table.getColumns().addAll(idCol, roomCol, typeCol, guestCol, emailCol,
            checkInCol, checkOutCol, nightsCol, totalCol, statusCol);
        return table;
    }

    private void checkAvailability() {
        LocalDate checkIn = checkInPicker.getValue();
        LocalDate checkOut = checkOutPicker.getValue();

        progressIndicator.setVisible(true);
        statusLabel.setText("Checking availability...");
        roomTable.setItems(FXCollections.observableArrayList());

        RoomAvailabilityTask task = new RoomAvailabilityTask(bookingService, checkIn, checkOut);

        task.setOnSucceeded(e -> {
            List<Room> rooms = task.getValue();
            roomTable.setItems(FXCollections.observableArrayList(rooms));
            statusLabel.setText(rooms.isEmpty()
                ? "No rooms available for the selected dates."
                : rooms.size() + " room(s) available.");
            progressIndicator.setVisible(false);
        });

        task.setOnFailed(e -> {
            statusLabel.setText("Error: " + task.getException().getMessage());
            progressIndicator.setVisible(false);
        });

        new Thread(task).start();
    }

    private void bookSelectedRoom() {
        Room selected = roomTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Room Selected", "Please select a room from the table first.");
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showAlert("Missing Information", "Please fill in all guest details.");
            return;
        }

        try {
            Guest guest = new Guest(0, name, email, phone);
            LocalDate checkIn = checkInPicker.getValue();
            LocalDate checkOut = checkOutPicker.getValue();
            int guests = guestSpinner.getValue();

            Booking booking = bookingService.createBooking(selected, guest, checkIn, checkOut, guests);

            showInfo("Booking Confirmed",
                "Booking #" + booking.getId() + " confirmed!\n\n" +
                "Room " + selected.getRoomNumber() + " (" + selected.getRoomType() + ")\n" +
                "Guest: " + name + "\n" +
                "Dates: " + checkIn + "  to  " + checkOut + "\n" +
                "Total: " + String.format("%.2f LE", booking.getTotalCost()));

            nameField.clear(); emailField.clear(); phoneField.clear();
            checkAvailability();
            loadBookings();

        } catch (RoomNotAvailableException | InvalidDateException | InvalidGuestCountException ex) {
            showAlert("Booking Failed", ex.getMessage());
        } catch (Exception ex) {
            showAlert("Error", "Unexpected error: " + ex.getMessage());
        }
    }

    private void cancelSelectedBooking() {
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Booking Selected", "Please select a booking to cancel.");
            return;
        }
        if ("CANCELLED".equals(selected.getStatus())) {
            showAlert("Already Cancelled", "This booking is already cancelled.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Cancel booking #" + selected.getId() + " for " + selected.getGuest().getName() + "?",
            ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Cancellation");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    bookingService.cancelBooking(selected.getId());
                    bookingStatusLabel.setText("Booking #" + selected.getId() + " cancelled.");
                    loadBookings();
                } catch (Exception ex) {
                    showAlert("Error", ex.getMessage());
                }
            }
        });
    }

    private void loadBookings() {
        try {
            bookingTable.setItems(FXCollections.observableArrayList(bookingService.getAllBookings()));
        } catch (Exception ex) {
            showAlert("Error", "Failed to load bookings: " + ex.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title); alert.setHeaderText(null); alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title); alert.setHeaderText(null); alert.showAndWait();
    }
}
