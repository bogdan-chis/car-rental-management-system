package repository.file.binary;

import domain.Car;
import exceptions.FileException;
import exceptions.ValidatorException;
import repository.file.FileRepository;
import validators.CarValidator;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CarRepositoryBinaryFile extends FileRepository<Integer, Car> {
    private final CarValidator validator = new CarValidator();

    public CarRepositoryBinaryFile(String filename) throws FileException {
        super(filename);
        readFromFile();
    }

    @Override
    protected void readFromFile() throws FileException, ValidatorException {
        try (FileInputStream fis = new FileInputStream(this.filename);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            HashMap<Integer, Car> deserializedElements;

            // Deserialize and cast
            try {
                deserializedElements = (HashMap<Integer, Car>) ois.readObject();
            } catch (ClassCastException | ClassNotFoundException e) {
                throw new FileException("Error deserializing data from file: " + filename + " - Invalid object type - " + e.getMessage());
            }

            for (Map.Entry<Integer, Car> entry : deserializedElements.entrySet()) {
                try {
                    validator.validateCar(entry.getValue());
                    this.elements.put(entry.getKey(), entry.getValue());
                } catch (ValidatorException e) {
                    throw new ValidatorException("Error reading from binary file. Invalid car: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new FileException("Error reading from binary file: " + filename + " - " + e.getMessage());
        }
    }

    @Override
    protected void writeToFile() throws FileException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.filename))) {
            oos.writeObject(this.elements);
        } catch (IOException e) {
            throw new FileException("Error writing to binary file: " + filename + " - " + e.getMessage());
        }
    }
}
