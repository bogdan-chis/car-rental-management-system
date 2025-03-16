package service;

import domain.Car;
import exceptions.CarException;
import exceptions.ValidatorException;
import filters.AbstractFilter;
import repository.base.IRepository;
import validators.CarValidator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CarService {
    private IRepository<Integer, Car> repository;
    private final CarValidator validator;

    public CarService(IRepository<Integer, Car> repo, CarValidator validator){
        this.repository = repo;
        this.validator = validator;
    }

    public void add(Car car) throws ValidatorException {
        try {
            validator.validateCar(car);
            if (repository.findById(car.getId()).isPresent()) {
                throw new ValidatorException("Invalid ID. A car with id = " + car.getId() + " already exists.");
            }
            repository.add(car.getId(), car);
        } catch (CarException e) {
            throw new ValidatorException(e.getMessage());
        }
    }


    public Iterable<Car> getAll() throws ValidatorException{
        Iterable<Car> allCars = repository.getAll();
        if (! allCars.iterator().hasNext())
            throw new ValidatorException("\tThere are no cars.");
        return allCars;
    }

    public List<Car> getCarList(){
        List<Car> carList = new ArrayList<>();
        Iterator<Car> iterator = repository.getIterator();
        while (iterator.hasNext())
            carList.add(iterator.next());
        return carList;
    }

    public Car delete(int id) throws ValidatorException {
        try {
            validator.validateId(id);
            Optional<Car> car = repository.findById(id);
            if (car.isEmpty()) {
                throw new ValidatorException("\tInvalid ID. There is no car with id = " + id + ".");
            }
            return repository.delete(id).get();

        } catch (CarException e) {
            throw new ValidatorException(e.getMessage());
        }
    }

    public void modify(Car car) throws ValidatorException {
        try {
            validator.validateCar(car);
            if (repository.findById(car.getId()).isEmpty())
                throw new ValidatorException("\tInvalid ID. There is no car with id = " + car.getId() + " that can be updated");
            repository.modify(car.getId(), car);
        } catch(CarException e){
            throw new ValidatorException(e.getMessage());
        }
    }

    public Car findByID(int id) throws ValidatorException {
        try {
            validator.validateId(id);
            if (repository.findById(id).isEmpty())
                throw new ValidatorException("\tInvalid ID. There is no car with id = " + id + ".");
            return repository.findById(id).get();
        } catch (CarException e) {
            throw new ValidatorException(e.getMessage());
        }
    }

    public List<Car> filter(Predicate<Car> cond){
        List<Car> filteredList = new ArrayList<>();
        Iterator<Car> iterator = repository.getIterator();

        while (iterator.hasNext()){
            Car car = iterator.next();
            if (cond.test(car))
                filteredList.add(car);
        }
        return filteredList;
    }
}
