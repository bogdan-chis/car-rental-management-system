package gui;

import domain.Reservation;
import filters.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import service.CarService;
import service.ReservationService;
import validators.CarValidator;
import validators.ReservationValidator;

import java.time.LocalDate;
import java.util.List;

public class ReservationController {
    private ObservableList<Reservation> reservationList;

    private ObservableList<Reservation> filteredList;

    private ReservationService reservationService;

    private CarService carService;

    private final ReservationValidator reservationValidator = new ReservationValidator();

    private final CarValidator carValidator = new CarValidator();




    @FXML
    private Button buttonAddReservation;

    @FXML
    private Button buttonDeleteReservation;

    @FXML
    private Button buttonUpdateReservation;

    @FXML
    private Label labelCarID;

    @FXML
    private Label labelEndDate;

    @FXML
    private Label labelReservationID;

    @FXML
    private Label labelStartDate;

    @FXML
    private ListView<Reservation> listViewReservations;

    @FXML
    private TextArea textAreaCarID;

    @FXML
    private TextArea textAreaEndDate;

    @FXML
    private TextArea textAreaOutput;

    @FXML
    private TextArea textAreaReservationID;

    @FXML
    private TextArea textAreaStartDate;

    @FXML
    private TextArea textAreaSearch;

    @FXML
    private MenuItem menuItemFilterByCar;

    @FXML
    private MenuItem menuItemFilterByEndDate;

    @FXML
    private MenuItem menuItemFilterByStartDate;



    public ReservationController(CarService carService, ReservationService reservationService){
        this.reservationService = reservationService;
        this.carService = carService;
    }


    @FXML
    void onKeyReleasedSearch(KeyEvent event) {
        String searchText = textAreaSearch.getText().trim().toLowerCase();

        if (searchText.isEmpty()){
            listViewReservations.setItems(reservationList);
        }
        else{
            List<Reservation> searchList = reservationService.filter(
                    reservation -> reservation.getCar().getBrand().toLowerCase().contains(searchText) ||
                            reservation.getCar().getModel().toLowerCase().contains(searchText)
            );

            filteredList = FXCollections.observableList(searchList);
            listViewReservations.setItems(filteredList);

        }

    }

    public void initialize() {
        reservationList = FXCollections.observableList(reservationService.getReservationsList());
        listViewReservations.setItems(reservationList);

        listViewReservations.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Reservation reservation, boolean empty) {
                super.updateItem(reservation, empty);
                if (empty || reservation == null) {
                    setText(null);
                } else {

                    setText(+reservation.getId() + " (" +
                            reservation.getCar().getBrand() + ", " +
                            reservation.getCar().getModel() + ", " +
                            reservation.getCar().getYear() + ", " +
                            reservation.getStartDate() + " to " +
                            reservation.getEndDate() + ")");
                }
            }
        });
        listViewReservations.setPrefSize(700, 300);

        buttonAddReservation.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    int rid = Integer.parseInt(textAreaReservationID.getText());
                    int cid = Integer.parseInt(textAreaCarID.getText());
                    carValidator.validateId(cid);

                    String startDate = textAreaStartDate.getText();
                    String endDate = textAreaEndDate.getText();

                    Reservation reservation = new Reservation(rid,
                            carService.findByID(cid),
                            startDate,
                            endDate
                    );
                    reservationValidator.validateReservation(reservation);

                    reservationService.add(reservation);
                    reservationList.add(reservation);

                    textAreaOutput.setText("New reservation added: " + reservation);
                } catch (Exception e) {
                    throwAlert(e.getMessage(), "Adding");
                }
                clearFields();
            }
        });

        buttonDeleteReservation.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    int rid = Integer.parseInt(textAreaReservationID.getText());
                    reservationValidator.validateId(rid);

                    Reservation deletedReservation = reservationService.findById(rid);
                    reservationList.remove(deletedReservation);
                    reservationService.delete(rid);

                    textAreaOutput.setText(deletedReservation + " deleted successfully.");
                } catch (Exception e) {
                    throwAlert(e.getMessage(), "Deleting");
                }
                clearFields();
            }
        });

        buttonUpdateReservation.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    int rid = Integer.parseInt(textAreaReservationID.getText());
                    int cid = Integer.parseInt(textAreaCarID.getText());
                    String startDate = textAreaStartDate.getText();
                    String endDate = textAreaEndDate.getText();

                    Reservation newReservation = new Reservation(rid,
                            carService.findByID(cid),
                            startDate,
                            endDate
                    );
                    reservationValidator.validateReservation(newReservation);

                    Reservation oldReservation = reservationService.findById(rid);
                    reservationService.update(newReservation);

                    int oldReservationIndex = reservationList.indexOf(oldReservation);
                    reservationList.set(oldReservationIndex, newReservation);

                    textAreaOutput.setText(newReservation + " updated successfully");

                } catch (Exception e) {
                    throwAlert(e.getMessage(), "Updating");
                }
                clearFields();
            }
        });

        menuItemFilterByCar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    int id = Integer.parseInt(textAreaCarID.getText());
                    carValidator.validateId(id);

                    AbstractFilter<Reservation> filterByCar = new FilterReservationsByCar(carService.findByID(id));

                    // IntelliJ said: replace lambda with method reference
                    filteredList = FXCollections.observableList(reservationService.filter(filterByCar::accept));
                    listViewReservations.setItems(filteredList);

                    textAreaOutput.setText("The reservations have been filtered by a given car id: " + id);


                } catch (Exception e) {
                    throwAlert(e.getMessage(), "Filter");
                }
                clearFields();
            }
        });

        menuItemFilterByStartDate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    String startDateString = textAreaStartDate.getText();

                    AbstractFilter<Reservation> filterByStartDate = new FilterReservationsBySD(LocalDate.parse(startDateString));

                    // IntelliJ said: replace lambda with method reference
                    filteredList = FXCollections.observableList(reservationService.filter(filterByStartDate::accept));
                    listViewReservations.setItems(filteredList);

                    textAreaOutput.setText("The reservations have been filtered by a given starting date: " + startDateString);


                } catch (Exception e) {
                    throwAlert(e.getMessage(), "Filter");
                }
                clearFields();
            }
        });

        menuItemFilterByEndDate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    String endDateString = textAreaEndDate.getText();

                    AbstractFilter<Reservation> filterByEndDate = new FilterReservationsByED(LocalDate.parse(endDateString));

                    // IntelliJ said: replace lambda with method reference
                    filteredList = FXCollections.observableList(reservationService.filter(filterByEndDate::accept));
                    listViewReservations.setItems(filteredList);

                    textAreaOutput.setText("The reservations have been filtered by a given ending date: " + endDateString);

                } catch (Exception e) {
                    throwAlert(e.getMessage(), "Filter");
                }
                clearFields();
            }
        });
    }

    private void clearFields(){
            textAreaReservationID.clear();
            textAreaCarID.clear();
            textAreaStartDate.clear();
            textAreaEndDate.clear();
    }

    private void throwAlert(String message, String operation){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(operation + " error");
            alert.setHeaderText("Invalid input");
            alert.setContentText(message);
            alert.showAndWait();
    }
}
