package ui;

import domain.Car;
import domain.Identifiable;
import domain.Reservation;
import exceptions.CarException;
import exceptions.ValidatorException;
import service.CarService;
import service.ReportService;
import service.ReservationService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UI {
    private static CarService carService;
    private static ReservationService reservationService;
    private static ReportService reportService;
    private static Scanner scanner;
    private static String repoType;

    public UI(CarService carService, ReservationService reservationService, ReportService reportService, String repoType) {
        UI.carService = carService;
        UI.reservationService = reservationService;
        UI.repoType = repoType;
        UI.reportService = reportService;

        scanner = new Scanner(System.in);
    }

    public void run(){
        while(true){
            printMainMenu();
            int mainOption = scanner.nextInt();
            if(mainOption == 1)
                exit();
            else if (mainOption == 2)
               CarsManagement();
            else if (mainOption == 3)
                ReservationsManagement();
            else if (mainOption == 4)
                ReportsManagement();
            else
                System.out.println("Invalid option");
        }
    }

    private static void CarsManagement(){
        boolean goBack = false;
        while(! goBack){
            printCarsMenu();
            int option = scanner.nextInt();
            if (option == 1)
                goBack = true;
            else if (option == 2)
                displayAllCars();
            else if (option == 3)
                addCar();
            else if (option == 4)
                deleteCar();
            else if (option == 5)
                modifyCar();
            else
                System.out.println("Invalid option");
        }
    }

    private static void addCar(){
        try {
            Car carToAdd = getCarFromUser();
            carService.add(carToAdd);
            System.out.println("The " + carToAdd.getBrand() + " " + carToAdd.getModel() + "("+ carToAdd.getYear() + ") has been added succesfully.");
        } catch (ValidatorException e) {
            System.out.println(e.getMessage());
            System.out.println("!! WARNING: Adding operation has failed !!");
        }
    }

    private static void displayAllCars(){
        try{
            printIdentifiable(carService.getAll());
        } catch (ValidatorException e){
            System.out.println(e.getMessage());
            System.out.println("!! WARNING: Display operation has failed !!");
        }
    }

    private static void deleteCar() {
        try {
            int id = getCarId();
            Car car = carService.delete(id);
            List<Reservation> delReservations = reservationService.deleteByCarId(id);
            System.out.println("The " + car + " has been deleted successfully.\n" +
                        delReservations + " associated reservation(s) have also been deleted.");
            } catch (ValidatorException e){
            System.out.println(e.getMessage());
            if (! e.getMessage().contains("No reservations found"))
                System.out.println("!! WARNING: Delete operation has failed !!");
        }
    }

    private static void modifyCar(){
        try {
            Car carToUpdate = getCarFromUser();
            carService.modify(carToUpdate);
            System.out.println("The car with id=" + carToUpdate.getId() + " has been updated succesfully.");
        } catch (ValidatorException e){
            System.out.println(e.getMessage());
            System.out.println("!! WARNING: Update operation has failed !!");
        }
    }


    private static Car getCarFromUser(){
        int id = getCarId();
        String brand = getCarBrand();
        String model = getCarModel();
        int year = getCarYear();

        return new Car(id, brand, model, year);

    }

    private static int getCarId(){
        try {
            System.out.println("Please enter the car ID: ");
            return scanner.nextInt();
        } catch (InputMismatchException e)
        {
            System.out.println("\tThis is not a valid ID (number)!");
            scanner.nextLine();
            return getCarId();
        }
    }

    private static String getCarBrand(){
        try {
            System.out.println("Please enter the brand: ");

            return scanner.next();
        } catch (InputMismatchException e){
            System.out.println("\tThis is not a valid brand (String) !!");
            scanner.nextLine();
            return getCarBrand();
        }
    }

    private static String getCarModel(){
        try {
            System.out.println("Please enter the model: ");

            return scanner.next();
        } catch (InputMismatchException e) {
            System.out.println("\tThis is not a valid model (String) !!");
            scanner.nextLine();
            return getCarModel();
        }
    }

    private static int getCarYear() {
        try {
            System.out.println("Please enter the year: ");
            return scanner.nextInt();
        } catch (InputMismatchException e)
        {
            System.out.println("\tThis is not a valid year (Integer) !!");
            scanner.nextLine();
            return getCarYear();
        }
    }





        private static void ReservationsManagement(){
        boolean goBack = false;
        while(! goBack){
            printReservationsMenu();
            int option = scanner.nextInt();
            if (option == 1)
                goBack = true;
            else if (option == 2)
                displayAllReservations();
            else if (option == 3)
                addReservation();
            else if (option == 4)
                deleteReservation();
            else if (option == 5)
                updateReservation();
            else
                System.out.println("Invalid option.");
        }
    }

    private static void addReservation() {
        try {
            Reservation reservationToAdd = getReservationFromUser();
            reservationService.add(reservationToAdd);
            System.out.println("The reservation with id=" + reservationToAdd.getId() + " from " + reservationToAdd.getStartDate() + " to " + reservationToAdd.getEndDate() + " has been created succesfully.");
        }
        catch (ValidatorException e){
            System.out.println(e.getMessage());
            System.out.println("!! WARNING: Adding operation has failed !!");
        }
    }

    private static void displayAllReservations(){
        try {
            printIdentifiable(reservationService.getAll());
        } catch (ValidatorException e){
            System.out.println(e.getMessage());
            System.out.println("!! WARNING: Display operation has failed !!");
        }
    }

    private static void deleteReservation(){
        try {
            int id = getReservationId();
            Reservation reservation = reservationService.delete(id);
            System.out.println("The " + reservation + " has been deleted successfully.");
            }
        catch (ValidatorException e) {
            System.out.println(e.getMessage());
            System.out.println("!! WARNING: Delete operation has failed !!");
        }
    }

    private static void updateReservation() {
        try {
            Reservation newReservation = getReservationFromUser();
            reservationService.update(newReservation);
            System.out.println("The reservation with id=" + newReservation.getId() + " has been updated successfully.");
        } catch (ValidatorException e) {
            System.out.println(e.getMessage());
            System.out.println("!! WARNING: Update operation has failed !!");
        }
    }



    private static Reservation getReservationFromUser() throws CarException {
        System.out.println("\tPlease enter the reservation info: ");
        int reservationId = getReservationId();
        int reservedCarId = getCarId();

        try {
            Car reservedCar = carService.findByID(reservedCarId);
            LocalDate startDate = getDateFromUser();
            LocalDate endDate = getDateFromUser();
            return new Reservation(reservationId, reservedCar, startDate, endDate);
        } catch (ValidatorException e){
            System.out.println(e.getMessage());
            return getReservationFromUser();
        }
    }


    private static int getReservationId() {
        try {
            System.out.println("Please enter the reservation ID: ");
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("\tThis is not a valid ID (Integer) !");
            scanner.nextLine();
            return getReservationId();
        }
    }

    private static LocalDate getDateFromUser() {
        LocalDate date = null;
        boolean validDate = false;

        while (!validDate) {
            System.out.println("Please enter a date (dd/MM/yyyy): ");
            String startDateStr = scanner.next();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            try {
                date = LocalDate.parse(startDateStr, formatter);
                validDate = true;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter the date in the format DD/MM/YYYY.");
                return getDateFromUser();
            }
        }
        return date;
    }


    private static void ReportsManagement(){
        boolean goBack = false;
        while(! goBack) {
            printReportsMenu();
            int option = scanner.nextInt();
            if (option == 1)
                goBack = true;
            else if (option == 2)
                displayAllReservationFromACar();
            else if (option == 3)
                displayCurrentlyReserverdCars();
            else if (option == 4)
                displayReservationsForCarInDateRange();
            else if (option == 5)
              displayMostPopularCar();
            else if (option == 6)
                displayPastReservations();
            else if (option == 7)
                displayFutureReservations();
            else
                System.out.println("Invalid option.");
        }
    }

    private static void displayAllReservationFromACar() {
        try {
            int carID = getCarId();
            List<String> reservations = reportService.getAllReservationsForCar(carID);

            if (reservations.isEmpty()) {
                System.out.println("No reservations found for car ID " + carID + ".");
            } else {
                System.out.println("Reservations for Car ID " + carID + ":");
                reservations.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void displayCurrentlyReserverdCars(){
        try {
            String currentDate = LocalDate.now().toString();
            List<String> reservedCars = reportService.getReservedCarsAtGivenDate(currentDate);

            if (reservedCars.isEmpty()) {
                System.out.println("No cars are currently reserved on " + currentDate + ".");
            } else {
                System.out.println("Currently reserved cars on " + currentDate + ":");
                reservedCars.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void displayReservationsForCarInDateRange() {
        int carId = getCarId();
        String startDate = getDateFromUser().toString();
        String endDate = getDateFromUser().toString();

        List<String> reservations = reportService.getReservationsForCarInDateRange(carId, startDate, endDate);
        if (reservations.isEmpty()) {
            System.out.println("No reservations found for the specified car and date range.");
        } else {
            System.out.println("Reservations for car ID " + carId + ":");
            reservations.forEach(System.out::println);
        }
    }

    private static void displayMostPopularCar() {
        List<String> mostPopularCar = reportService.getMostPopularCar();
        if (mostPopularCar.isEmpty()) {
            System.out.println("The most popular car is: " + mostPopularCar);
        } else {
            System.out.println("No reservations have been made yet.");
        }
    }

    private static void displayPastReservations() {
        String currentDate = LocalDate.now().toString();
        List<String> pastReservations = reportService.getPastReservations(currentDate);

        if (pastReservations.isEmpty()) {
            System.out.println("No past reservations found.");
        } else {
            System.out.println("Past Reservations:");
            pastReservations.forEach(System.out::println);
        }
    }

    private static void displayFutureReservations() {
        String currentDate = LocalDate.now().toString();
        List<String> futureReservations = reportService.getFutureReservations(currentDate);

        if (futureReservations.isEmpty()) {
            System.out.println("No future reservations found.");
        } else {
            System.out.println("Future Reservations:");
            futureReservations.forEach(System.out::println);
        }
    }







    private static void printMainMenu(){
        System.out.println("=====================================");
        printRepositoryInfo();
        System.out.println("--- Car Rental Menu --- \n");
        System.out.println("1. Exit");
        System.out.println("2. View Car Menu");
        System.out.println("3. View Rental Menu");
        System.out.println("4. View various reports");
        System.out.println("=====================================");
    }

    private static void printCarsMenu(){
        System.out.println("=====================================");
        printRepositoryInfo();
        System.out.println("--- Car Management Menu --- \n");
        System.out.println("1. Go back to main menu");
        System.out.println("2. Display all cars");
        System.out.println("3. Add a new car");
        System.out.println("4. Delete a car");
        System.out.println("5. Update a car");
        System.out.println("=====================================");
    }

    private static void printReservationsMenu(){
        System.out.println("=====================================");
        printRepositoryInfo();
        System.out.println("--- Reservations Management Menu --- \n");
        System.out.println("1. Go back to main menu");
        System.out.println("2. Display all reservations");
        System.out.println("3. Add a new reservation");
        System.out.println("4. Delete a reservation");
        System.out.println("5. Update a reservation");
        System.out.println("=====================================");
    }

    private static void printReportsMenu(){
        System.out.println("=====================================");
        System.out.println("1. Go back to main menu");
        System.out.println("2. View all reservations from a car.");
        System.out.println("3. View all cars currently reserved.");
        System.out.println("4. View reservations for a specific car within a date range.");
        System.out.println("5. Find the most popular car.");
        System.out.println("6. View all past reservations.");
        System.out.println("7. View all future reservations.");
        System.out.println("=====================================");
    }

    private static void printIdentifiable(Iterable<? extends Identifiable<Integer>> elements){
        for (Identifiable<Integer> entity: elements)
            System.out.println(entity.toString());
    }

    private static void printRepositoryInfo(){
        System.out.println("---- Current repository type: " + repoType.toUpperCase() + " ---- \n");
    }

    private static void exit(){
        System.out.println("Exiting...");
        System.exit(0);
    }

}
