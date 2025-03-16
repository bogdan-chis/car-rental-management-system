package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import service.CarService;
import service.ReportService;
import service.ReservationService;
import validators.CarValidator;

import java.time.LocalDate;
import java.util.List;


public class ReportController {

    private CarService carService;
    private ReservationService reservationService;
    private ReportService reportService;

    private final CarValidator carValidator = new CarValidator();

    ObservableList<String> stringObservableList;

    @FXML
    private Label labelCarID;

    @FXML
    private Label labelEndDate;

    @FXML
    private Label labelStartDate;

    @FXML
    private ListView<String> listViewString;

    @FXML
    private MenuItem menuItem1;

    @FXML
    private MenuItem menuItem2;

    @FXML
    private MenuItem menuItem3;

    @FXML
    private MenuItem menuItem4;

    @FXML
    private MenuItem menuItem5;

    @FXML
    private MenuItem menuItem6;

    @FXML
    private MenuItem menuItem7;

    @FXML
    private TextArea textAreaCarID;

    @FXML
    private TextArea textAreaEndDate;

    @FXML
    private TextArea textAreaOutput;

    @FXML
    private TextArea textAreaStartDate;


    public ReportController(CarService carService, ReservationService reservationService, ReportService reportService){
        this.carService = carService;
        this.reservationService = reservationService;
        this.reportService = reportService;
    }

    public void initialize(){

        menuItem1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    int id = Integer.parseInt(textAreaCarID.getText());
                    carValidator.validateId(id);

                    stringObservableList = FXCollections.observableList(reportService.getAllReservationsForCar(id));
                    listViewString.setItems(stringObservableList);
                }catch (Exception e){
                    throwAlert(e.getMessage(), "Report 1");
                }
                clearFields();
            }
        });

        menuItem2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    int id = Integer.parseInt(textAreaCarID.getText());
                    carValidator.validateId(id);

                    String startDateString = textAreaStartDate.getText();
                    String endDateString = textAreaEndDate.getText();

                    stringObservableList = FXCollections.observableList(reportService.getReservationsForCarInDateRange(id, startDateString, endDateString));
                    listViewString.setItems(stringObservableList);
                }catch (Exception e){
                    throwAlert(e.getMessage(), "Report 2");
                }
                clearFields();

            }
        });

        menuItem3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    String currentDate = LocalDate.now().toString();
                    stringObservableList = FXCollections.observableList(reportService.getPastReservations(currentDate));
                    listViewString.setItems(stringObservableList);

                }catch (Exception e){
                    throwAlert(e.getMessage(), "Report 3");
                }
                clearFields();

            }
        });

        menuItem4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    String currentDate = LocalDate.now().toString();
                    stringObservableList = FXCollections.observableList(reportService.getFutureReservations(currentDate));
                    listViewString.setItems(stringObservableList);

                }catch (Exception e){
                    throwAlert(e.getMessage(), "Report 4");
                }
                clearFields();

            }
        });

        menuItem5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    stringObservableList = FXCollections.observableList(reportService.getMostPopularCar());
                    listViewString.setItems(stringObservableList);
                }catch (Exception e){
                    throwAlert(e.getMessage(), "Report 5");
                }
                clearFields();
            }
        });

        menuItem6.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    String currentDate = LocalDate.now().toString();
                    stringObservableList = FXCollections.observableList(reportService.getReservedCarsAtGivenDate(currentDate));
                    listViewString.setItems(stringObservableList);

                }catch (Exception e){
                    throwAlert(e.getMessage(), "Report 6");
                }
                clearFields();
            }
        });

        menuItem7.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    String currentDate = LocalDate.now().toString();
                    stringObservableList = FXCollections.observableList(reportService.getAvailableCarsNow(currentDate));
                    listViewString.setItems(stringObservableList);

                }catch (Exception e){
                    throwAlert(e.getMessage(), "Report 7");
                }
                clearFields();
            }
        });
    }

    private void throwAlert(String message, String operation){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(operation + " error");
        alert.setHeaderText("Invalid input");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields(){
        textAreaCarID.clear();
        textAreaStartDate.clear();
        textAreaEndDate.clear();
    }
}
