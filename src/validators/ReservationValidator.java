package validators;

import domain.Car;
import domain.Reservation;
import exceptions.ValidatorException;

import java.time.LocalDate;

public class ReservationValidator {
    private final CarValidator carValidator = new CarValidator();

    public boolean validateReservation(Reservation r) throws ValidatorException {
        if (r == null) {
            throw new ValidatorException("\tInvalid reservation: Reservation object cannot be null.");
        }
        return validateId(r.getId())
                && validateCar(r.getCar())
                && validateStartDate(r.getStartDate())
                && validateEndDate(r.getStartDate(), r.getEndDate());
    }

    public boolean validateId(int id) throws ValidatorException {
        if (id < 0 || id > 9999) {
            throw new ValidatorException("\tInvalid ID: ID must be between 0 and 9999.");
        }
        return true;
    }

    public boolean validateCar(Car car) throws ValidatorException {
        if (car == null) {
            throw new ValidatorException("\tInvalid car: Car object cannot be null.");
        }
        try {
            carValidator.validateCar(car);
        } catch (ValidatorException e) {  // Catch ValidatorException from CarValidator
            throw new ValidatorException(e.getMessage());
        }
        return true;
    }

    public boolean validateStartDate(LocalDate startDate) throws ValidatorException {
        if (startDate == null) {
            throw new ValidatorException("\tInvalid Start Date: Start date cannot be null.");
        }
        return true;
    }

    public boolean validateEndDate(LocalDate startDate, LocalDate endDate) throws ValidatorException {
        if (endDate == null) {
            throw new ValidatorException("\tInvalid End Date: End date cannot be null.");
        }
        if (endDate.isBefore(startDate)) {
            throw new ValidatorException("\tInvalid End Date: End date cannot be before the start date.");
        }
        if (endDate.equals(startDate)) {
            throw new ValidatorException("\tInvalid End Date: End date cannot be the same as the start date.");
        }
        return true;
    }
}