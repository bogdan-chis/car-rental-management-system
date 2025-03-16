package validators;

import domain.Car;
import exceptions.ValidatorException;

public class CarValidator {

    public CarValidator() {}

    public boolean validateCar(Car car) throws ValidatorException {
        if (car == null) {
            throw new ValidatorException("\tInvalid car: Car object cannot be null");
        }
        return validateId(car.getId()) &&
                validateBrand(car.getBrand()) &&
                validateModel(car.getModel()) &&
                validateYear(car.getYear());
    }

    public boolean validateId(int id) throws ValidatorException {
        if (id < 0 || id > 9999) {
            throw new ValidatorException("\tInvalid ID: ID must be between 0 and 9999");
        }
        return true;
    }

    public boolean validateBrand(String brand) throws ValidatorException {
        if (brand == null || brand.trim().isEmpty()) {
            throw new ValidatorException("\tInvalid brand: Brand cannot be empty");
        }
        return true;
    }

    public boolean validateModel(String model) throws ValidatorException {
        if (model == null || model.trim().isEmpty()) {
            throw new ValidatorException("\tInvalid model: Model cannot be empty");
        }
        return true;
    }

    public boolean validateYear(int year) throws ValidatorException {
        if (year < 1886 || year > 2025) {
            throw new ValidatorException("\tInvalid year: Year must be between 1886 and 2025");
        }
        return true;
    }
}
