package repository.factory;

import domain.Car;
import domain.Identifiable;
import domain.Reservation;
import exceptions.CarException;
import exceptions.ReservationException;
import repository.base.IRepository;
import repository.database.CarRepositoryDB;
import repository.database.ReservationRepositoryDB;
import repository.file.binary.CarRepositoryBinaryFile;
import repository.file.binary.ReservationRepositoryBinaryFile;
import repository.file.json.CarRepositoryJSONFile;
import repository.file.json.ReservationRepositoryJSONFile;
import repository.file.text.CarRepositoryTextFile;
import repository.file.text.ReservationRepositoryTextFile;
import repository.file.xml.CarRepositoryXMLFile;
import repository.file.xml.ReservationRepositoryXMLFile;
import repository.specific.CarRepository;
import repository.specific.ReservationRepository;
import settings.Settings;

public class FactoryRepository<ID, T extends Identifiable<ID>> {
    public static IRepository<Integer, Car> createCarRepository(Settings settings) throws CarException {

        String repositoryType = settings.getProperty("Repository");
        String carFileInput = settings.getProperty("Cars");
        switch (repositoryType) {
            case "binary":
                return new CarRepositoryBinaryFile(carFileInput);
            case "memory":
                return new CarRepository();
            case "text":
                return new CarRepositoryTextFile(carFileInput);
            case "database":
                String DBLocation = settings.getProperty("Location");
                return new CarRepositoryDB(DBLocation);
            case "json":
                return new CarRepositoryJSONFile(carFileInput);
            case "xml":
                return new CarRepositoryXMLFile(carFileInput);
            default:
                throw new CarException("Invalid repository type: " + repositoryType);
        }
    }

    public static IRepository<Integer, Reservation> createReservationRepository(Settings settings) throws ReservationException {
        String repositoryType = settings.getProperty("Repository");
        String reservationsFileInput = settings.getProperty("Reservations");
        switch (repositoryType) {
            case "binary":
                return new ReservationRepositoryBinaryFile(reservationsFileInput);
            case "memory":
                return new ReservationRepository();
            case "text":
                return new ReservationRepositoryTextFile(reservationsFileInput);
            case "database":
                String DBLocation = settings.getProperty("Location");
                return new ReservationRepositoryDB(DBLocation);
            case "json":
                return new ReservationRepositoryJSONFile(reservationsFileInput);
            case "xml":
                return new ReservationRepositoryXMLFile(reservationsFileInput);
            default:
                throw new ReservationException("Invalid repository type: " + repositoryType);
        }
    }
}


