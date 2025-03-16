package repository.file.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import domain.Car;
import exceptions.CarException;
import repository.base.IRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class CarRepositoryJSONFile implements IRepository<Integer, Car> {
    private final String filePath;
    private final ObjectMapper objectMapper;

    public CarRepositoryJSONFile(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();

        // Register modules and configure pretty-printing and date handling
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    // Method to load cars from JSON file
    private List<Car> loadCars() throws CarException {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Car.class));
            }
            return new ArrayList<>(); // Return an empty list if file doesn't exist
        } catch (IOException e) {
            throw new CarException("Error loading cars from JSON file: " + e.getMessage());
        }
    }

    // Method to save cars to JSON file
    private void saveCars(List<Car> cars) throws CarException {
        try {
            objectMapper.writeValue(new File(filePath), cars);
        } catch (IOException e) {
            throw new CarException("Error saving cars to JSON file: " + e.getMessage());
        }
    }

    @Override
    public void add(Integer id, Car car) throws CarException {
        List<Car> cars = loadCars();
        cars.add(car);
        saveCars(cars);
    }

    @Override
    public Optional<Car> delete(Integer id) throws CarException {
        List<Car> cars = loadCars();

        Optional<Car> carToDelete = cars.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();

        if (carToDelete.isPresent()) {
            cars.remove(carToDelete.get());
            saveCars(cars);
        }
        return carToDelete;
    }

    @Override
    public void modify(Integer id, Car updatedCar) throws CarException {
        List<Car> cars = loadCars();
        boolean carFound = false;

        for (int i = 0; i < cars.size(); i++) {
            if (cars.get(i).getId().equals(id)) {
                cars.set(i, updatedCar); // Replace the existing car with the updated car
                carFound = true;
                break;
            }
        }

        if (!carFound) {
            throw new CarException("Car with ID " + id + " not found for modification.");
        }

        saveCars(cars);
    }

    @Override
    public Optional<Car> findById(Integer id) throws CarException {
        List<Car> cars = loadCars();
        return cars.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    @Override
    public Iterable<Car> getAll() throws CarException {
        return loadCars();
    }

    @Override
    public Iterator<Car> getIterator() {
        try {
            return loadCars().iterator();
        } catch (CarException e) {
            throw new RuntimeException("Failed to get iterator: " + e.getMessage(), e);
        }
    }

    @Override
    public int size() throws CarException {
        return loadCars().size();
    }
}
