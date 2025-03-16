package gui;

import domain.Car;
import domain.Reservation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.base.IRepository;
import repository.factory.FactoryRepository;
import service.CarService;
import service.ReportService;
import service.ReservationService;
import settings.Settings;
import validators.CarValidator;
import validators.ReservationValidator;


public class RentalGUI extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Settings settings = new Settings("src/settings/settings.properties");
        String repoType = settings.getProperty ("Repository");

        IRepository<Integer, Car> carRepository = FactoryRepository.createCarRepository(settings);
        IRepository<Integer, Reservation> reservationRepository = FactoryRepository.createReservationRepository(settings);
        
        CarValidator carValidator = new CarValidator();
        ReservationValidator reservationValidator = new ReservationValidator();

        CarService carService = new CarService(carRepository, carValidator);
        ReservationService reservationService = new ReservationService(reservationRepository, reservationValidator);
        ReportService reportService = new ReportService(carRepository, reservationRepository);

        RentalController rentalController = new RentalController(carService, reservationService, reportService);
        FXMLLoader loader = new FXMLLoader(RentalGUI.class.getResource("RentalGUI.fxml"));
        loader.setController(rentalController);

        Scene scene = new Scene(loader.load());
        stage.setTitle("Car rental application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}