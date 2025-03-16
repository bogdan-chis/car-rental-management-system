package main;

import domain.Car;
import domain.Reservation;
import repository.base.IRepository;
import repository.factory.FactoryRepository;
import service.CarService;
import service.ReportService;
import service.ReservationService;
import settings.Settings;
import ui.UI;
import validators.CarValidator;
import validators.ReservationValidator;

public class Main {
    public static void main(String[] args) {
        Settings settings = new Settings("src/settings/settings.properties");
        String repoType = settings.getProperty("Repository");

        IRepository<Integer, Car> carRepository = FactoryRepository.createCarRepository(settings);
        IRepository<Integer, Reservation> reservationRepository = FactoryRepository.createReservationRepository(settings);


        CarValidator carValidator = new CarValidator();
        ReservationValidator reservationValidator = new ReservationValidator();

        CarService carService = new CarService(carRepository, carValidator);
        ReservationService reservationService = new ReservationService(reservationRepository, reservationValidator);
        ReportService reportService = new ReportService(carRepository, reservationRepository);

        UI ui =  new UI(carService, reservationService, reportService, repoType);
        ui.run();

    }
}
