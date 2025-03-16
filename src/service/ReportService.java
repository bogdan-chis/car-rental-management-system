package service;

import domain.Car;
import domain.Reservation;
import repository.base.IRepository;
import utils.StreamUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReportService {
    private final IRepository<Integer, Car> carRepository;
    private final IRepository<Integer, Reservation> reservationRepository;

    public ReportService(IRepository<Integer, Car> carRepository, IRepository<Integer, Reservation> reservationRepository) {
        this.carRepository = carRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<String> getAllReservationsForCar(Integer carId) {
        return StreamUtils.toStream(reservationRepository.getAll())
                .filter(reservation -> reservation.getCar().getId().equals(carId))
                .map(reservation -> String.format("Reservation ID: %d, Start: %s, End: %s",
                        reservation.getId(),
                        reservation.getStartDate(),
                        reservation.getEndDate()))
                .collect(Collectors.toList());
    }

    public List<String> getReservedCarsAtGivenDate(String currentDate) {
        return StreamUtils.toStream(reservationRepository.getAll())
                .filter(reservation -> currentDate.compareTo(reservation.getStartDate().toString()) >= 0 &&
                        currentDate.compareTo(reservation.getEndDate().toString()) <= 0)
                .map(reservation -> "[" + reservation.getCar().getId() + "] " + reservation.getCar().getBrand() + " " + reservation.getCar().getModel())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getReservationsForCarInDateRange(int carId, String startDate, String endDate) {
        return StreamUtils.toStream(reservationRepository.getAll())
                .filter(reservation -> reservation.getCar().getId() == carId)
                .filter(reservation -> {
                    String reservationStart = reservation.getStartDate().toString();
                    String reservationEnd = reservation.getEndDate().toString();
                    return !(endDate.compareTo(reservationStart) < 0 || startDate.compareTo(reservationEnd) > 0);
                })
                .map(reservation -> String.format("Reservation ID: %d, Start: %s, End: %s",
                        reservation.getId(),
                        reservation.getStartDate(),
                        reservation.getEndDate()))
                .collect(Collectors.toList());
    }

    public List<String> getMostPopularCar() {
        return StreamUtils.toStream(reservationRepository.getAll())
                .collect(Collectors.groupingBy(Reservation::getCar, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> {
                    Car car = entry.getKey();
                    return String.format("Most Popular Car: ID %d, Brand: %s, Model: %s, Reservations: %d",
                            car.getId(),
                            car.getBrand(),
                            car.getModel(),
                            entry.getValue());
                })
                .stream()
                .collect(Collectors.toList());
    }

    public List<String> getPastReservations(String currentDate) {
        return StreamUtils.toStream(reservationRepository.getAll())
                .filter(reservation -> reservation.getEndDate().toString().compareTo(currentDate) < 0)
                .map(reservation -> String.format("Reservation ID: %d, Start: %s, End: %s",
                        reservation.getId(),
                        reservation.getStartDate(),
                        reservation.getEndDate()))
                .collect(Collectors.toList());
    }

    public List<String> getFutureReservations(String currentDate) {
        return StreamUtils.toStream(reservationRepository.getAll())
                .filter(reservation -> reservation.getStartDate().toString().compareTo(currentDate) > 0)
                .map(reservation -> String.format("Reservation ID: %d, Start: %s, End: %s",
                        reservation.getId(),
                        reservation.getStartDate(),
                        reservation.getEndDate()))
                .collect(Collectors.toList());
    }

    public List<String> getAvailableCarsNow(String currentDate) {
        // Get IDs of cars currently reserved at the specified date
        List<Integer> reservedCarIds = StreamUtils.toStream(reservationRepository.getAll())
                .filter(reservation -> currentDate.compareTo(reservation.getStartDate().toString()) >= 0 &&
                        currentDate.compareTo(reservation.getEndDate().toString()) <= 0)
                .map(reservation -> reservation.getCar().getId())
                .distinct()
                .collect(Collectors.toList());

        // Get all cars that are not reserved
        return StreamUtils.toStream(carRepository.getAll())
                .filter(car -> !reservedCarIds.contains(car.getId()))
                .map(car -> String.format("Car ID: %d, Brand: %s, Model: %s",
                        car.getId(),
                        car.getBrand(),
                        car.getModel()))
                .collect(Collectors.toList());
    }
}
