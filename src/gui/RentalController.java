package gui;

import domain.Car;
import domain.Reservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import service.CarService;
import service.ReportService;
import service.ReservationService;
import validators.CarValidator;
import validators.ReservationValidator;

import java.io.IOException;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.function.BiConsumer;

public class RentalController {
    ObservableList<Reservation> reservationsList;
    ObservableList<Car> carList;

    protected CarService carService;
    protected ReservationService reservationService;
    protected ReportService reportService;

    protected CarValidator carValidator = new CarValidator();
    protected ReservationValidator reservationValidator = new ReservationValidator();


    @FXML
    private Button buttonCarMenu;

    @FXML
    private Button buttonReservationMenu;

    @FXML
    private Button buttonReportMenu;

    @FXML
    private Label labelCarID;

    @FXML
    private Label labelCrud;

    @FXML
    private Label labelCrudCars;

    @FXML
    private Label labelEndDate;

    @FXML
    private Label labelReservationID;

    @FXML
    private Label labelStartDate;

    @FXML
    private ListView<Reservation> listViewReservations;

    @FXML
    private ListView<Car> listViewCars;

    @FXML
    private TextArea textAreaOutput;

    public static BiConsumer<Car, List<Reservation>> deletedReservationsMessageCallback;


    public RentalController(CarService carService, ReservationService reservationService, ReportService reportService) {
        this.carService = carService;
        this.reservationService = reservationService;
        this.reportService = reportService;
    }

    public void initialize() {
        reservationsList = FXCollections.observableList(reservationService.getReservationsList());
        carList = FXCollections.observableList(carService.getCarList());

        listViewReservations.setItems(reservationsList);
        listViewCars.setItems(carList);

        deletedReservationsMessageCallback = this::updateMessageBox;

        listViewCars.setCellFactory(lv -> new ListCell<Car>() {
            @Override
            protected void updateItem(Car car, boolean empty) {
                super.updateItem(car, empty);
                if (empty || car == null) {
                    setText(null);
                } else {
                    setText(car.getId() + ". " + car.getBrand() + " " + car.getModel() + " " + car.getYear()) ;
                }
            }
        });

        listViewReservations.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Reservation reservation, boolean empty) {
                super.updateItem(reservation, empty);
                if (empty || reservation == null) {
                    setText(null);
                } else {

                    setText(+ reservation.getId() + " (" +
                            reservation.getCar().getBrand() + ", " +
                            reservation.getCar().getModel() + ", " +
                            reservation.getCar().getYear() + ", " +
                            reservation.getStartDate() + " to " +
                            reservation.getEndDate() + ")");
                }
            }
        });
        listViewReservations.setPrefSize(700, 300);

        buttonCarMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                CarController carController = new CarController(carService, reservationService);

                carController.setCarDeletionListener((deletedCar, deletedReservations) -> {
                    updateMessageBox(deletedCar, deletedReservations);
                });

                FXMLLoader loader = new FXMLLoader(getClass().getResource("CarGUI.fxml"));
                loader.setController(carController);

                Parent root = null;
                try {
                    root = loader.load();

                    Stage secondStage = new Stage();
                    secondStage.setTitle("Car Window");

                    Scene scene = new Scene(root);
                    secondStage.setScene(scene);
                    secondStage.show();
                    secondStage.setOnCloseRequest(event -> {
                        updateCarList();
                        updateReservationList();
                    });
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Loading error");
                    alert.setHeaderText("Error while opening car menu");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });

        buttonReservationMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ReservationController reservationController = new ReservationController(carService, reservationService);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ReservationGUI.fxml"));
                loader.setController(reservationController);

                Parent root = null;
                try {
                    root = loader.load();

                    Stage secondStage = new Stage();
                    secondStage.setTitle("Reservation Window");

                    Scene scene = new Scene(root);
                    secondStage.setScene(scene);
                    secondStage.show();
                    secondStage.setOnCloseRequest(event -> {
                        updateReservationList();
                    });

                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Loading error");
                    alert.setHeaderText("Error while opening reservation menu");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });

        buttonReportMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ReportController reportController = new ReportController(carService, reservationService, reportService);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportGUI.fxml"));
                loader.setController(reportController);

                Parent root = null;
                try {
                    root = loader.load();

                    Stage secondStage = new Stage();
                    secondStage.setTitle("Report Window");

                    Scene scene = new Scene(root);
                    secondStage.setScene(scene);
                    secondStage.show();

                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Loading error");
                    alert.setHeaderText("Error while opening report menu");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }

            }
        });
    }



    private void updateCarList() {
        List<Car> listOfCars = carService.getCarList();
        carList.setAll(listOfCars);
    }

    private void updateReservationList(){
        List<Reservation> listOfReservations = reservationService.getReservationsList();
        reservationsList.setAll(listOfReservations);
    }


    private void updateMessageBox(Car deletedCar, List<Reservation> deletedReservations) {
        StringBuilder message = new StringBuilder();
        if (!deletedReservations.isEmpty()) {
            message.append("The following reservations were deleted because the car ")
                    .append(deletedCar.getBrand())
                    .append(" ")
                    .append(deletedCar.getModel())
                    .append(" (")
                    .append(deletedCar.getYear())
                    .append(") was removed:\n");

            for (Reservation reservation : deletedReservations) {
                message.append("Reservation ID: ")
                        .append(reservation.getId())
                        .append(" | Dates: ")
                        .append(reservation.getStartDate())
                        .append(" to ")
                        .append(reservation.getEndDate())
                        .append("\n");
            }
        } else {
            message.append("No reservations were associated with the deleted car.");
        }
        textAreaOutput.setText(message.toString());
    }
}
