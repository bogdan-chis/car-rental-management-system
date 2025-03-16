package gui;


import domain.Car;
import domain.Reservation;
import exceptions.ValidatorException;
import filters.AbstractFilter;
import filters.FilterCarsByBrand;
import filters.FilterCarsByModel;
import filters.FilterCarsByYear;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import service.CarService;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import service.ReservationService;
import validators.CarValidator;

import java.util.List;
import java.util.function.BiConsumer;

public class CarController {

    private ObservableList<Car> carList;

    private ObservableList<Car> filteredCarList;

    private final CarValidator carValidator = new CarValidator();

    private CarService carService;

    private ReservationService reservationService;

    // callback
    private BiConsumer<Car, List<Reservation>> carDeleteListener;


    @FXML
    private Button buttonAddCar;

    @FXML
    private Button buttonDeleteCar;

    @FXML
    private Button buttonUpdateCar;

    @FXML
    private Label labelCarID;

    @FXML
    private Label labelCarModel;

    @FXML
    private Label labelProductionYear;

    @FXML
    private TextArea textAreaCarBrand;

    @FXML
    private TextArea textAreaCarID;

    @FXML
    private TextArea textAreaCarModel;

    @FXML
    private TextArea textAreaYear;

    @FXML
    private TextArea textAreaOutput;

    @FXML
    private TextArea textAreaSearch;

    @FXML
    private ListView<Car> listViewCars;

    @FXML
    private MenuButton menuButtonFilters;

    @FXML
    private MenuItem menuItemFilterBrand;

    @FXML
    private MenuItem menuItemFilterModel;

    @FXML
    private MenuItem menuItemFilterYear;

    @FXML
    void onKeyReleasedSearch(KeyEvent event) {
        String searchText = textAreaSearch.getText().trim().toLowerCase();
        if (searchText.isEmpty())
            listViewCars.setItems(carList);
        else{

            List<Car> searchList = carService.filter(
                    car -> car.getBrand().toLowerCase().contains(searchText) ||
                            car.getModel().toLowerCase().contains(searchText)
            );

            filteredCarList = FXCollections.observableList(searchList);
            listViewCars.setItems(filteredCarList);

        }
    }


    public CarController(CarService carService, ReservationService reservationService) {
        this.carService = carService;
        this.reservationService = reservationService;
    }

    public void initialize(){
        carList = FXCollections.observableList(carService.getCarList());
        listViewCars.setItems(carList);

        buttonAddCar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    int cid = Integer.parseInt(textAreaCarID.getText());
                    String brand = textAreaCarBrand.getText();
                    String model = textAreaCarModel.getText();
                    int year = Integer.parseInt(textAreaYear.getText());

                    Car carToAdd = new Car(cid, brand, model, year);
                    carValidator.validateCar(carToAdd);

                    carService.add(carToAdd);
                    carList.add(carToAdd);

                    textAreaOutput.setText("New car added: " + carToAdd);
                    clearFields();

                } catch (Exception e) {
                    throwAlert(e.getMessage(), "Adding");
                }
                clearFields();
            }
        });

        buttonDeleteCar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try{
                    int cid = Integer.parseInt(textAreaCarID.getText());
                    carValidator.validateId(cid);
                    Car carToDelete = carService.delete(cid);
                    carList.remove(carToDelete);

                    // !!!!!!!!!! also delete all reservations
                    List<Reservation> deletedReservations = reservationService.deleteByCarId(cid);

                    textAreaOutput.setText(carToDelete + " deleted successfully (+ all reservations)");
                    if (carDeleteListener != null)
                        carDeleteListener.accept(carToDelete, deletedReservations);

                } catch (Exception e) {
                    throwAlert(e.getMessage(), "Delete");
                }
                clearFields();
            }
        });

        buttonUpdateCar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try{
                    int cid = Integer.parseInt(textAreaCarID.getText());
                    String brand = textAreaCarBrand.getText();
                    String model = textAreaCarModel.getText();
                    int year = Integer.parseInt(textAreaYear.getText());

                    Car newCar = new Car(cid, brand, model, year);
                    carValidator.validateCar(newCar);

                    Car oldCar = carService.findByID(cid);
                    carService.modify(newCar);

                    int oldCarIndex = carList.indexOf(oldCar);
                    carList.set(oldCarIndex, newCar);

                    reservationService.updateReservationsForCar(cid, newCar);

                    textAreaOutput.setText(newCar + " updated successfully.");
                    clearFields();

                } catch (NumberFormatException e) {
                    clearFields();
                    throwAlert(e.getMessage(), "Update");
                }
            }
        });

        menuItemFilterBrand.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    String brand = textAreaCarBrand.getText();
                    carValidator.validateBrand(brand);

                    AbstractFilter<Car> filterByBrand = new FilterCarsByBrand(brand);
                    List<Car> filteredCars = carService.filter(filterByBrand::accept);

                    filteredCarList = FXCollections.observableList(filteredCars);
                    listViewCars.setItems(filteredCarList);
                    textAreaOutput.setText("The cars have been filtered by brand: " + brand);

                }catch (ValidatorException e) {
                    throwAlert(e.getMessage(), "Filter");
                }
                clearFields();
            }
        });

        menuItemFilterModel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    String model = textAreaCarModel.getText();
                    carValidator.validateModel(model);

                    AbstractFilter<Car> filterByModel = new FilterCarsByModel(model);

                    // IntelliJ said: replace lambda with method reference
                    List<Car> filteredCars = carService.filter(filterByModel::accept);
                    filteredCarList = FXCollections.observableList(filteredCars);

                    listViewCars.setItems(filteredCarList);
                    textAreaOutput.setText("The cars have been filtered by model: " + model);

                }catch (ValidatorException e) {
                    throwAlert(e.getMessage(), "Filter");
                }
                clearFields();
            }
        });

        menuItemFilterYear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    int year = Integer.parseInt(textAreaYear.getText());
                    carValidator.validateYear(year);

                    AbstractFilter<Car> filterByYear = new FilterCarsByYear(year);

                    // IntelliJ said: replace lambda with method reference
                    List<Car> filteredCars = carService.filter(filterByYear::accept);
                    filteredCarList = FXCollections.observableList(filteredCars);

                    listViewCars.setItems(filteredCarList);
                    textAreaOutput.setText("The cars have been filtered by year: " + year);

                }catch (Exception e) {
                    throwAlert(e.getMessage(), "Filter");
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

    public void setCarDeletionListener(BiConsumer<Car, List<Reservation>> listener){
        this.carDeleteListener = listener;
    }


    private void clearFields(){
            textAreaCarID.clear();
            textAreaCarBrand.clear();
            textAreaCarModel.clear();
            textAreaYear.clear();
    }
}
