package repository.file.text;

import domain.Car;
import exceptions.FileException;
import exceptions.ValidatorException;
import repository.file.FileRepository;
import validators.CarValidator;

import java.io.*;
import java.util.Iterator;

public class CarRepositoryTextFile extends FileRepository<Integer, Car> {
    private final CarValidator validator = new CarValidator();

    public CarRepositoryTextFile(String filename) throws FileException {
        super(filename);
        readFromFile();
    }

    @Override
    protected void readFromFile() throws FileException, ValidatorException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length != 4) {
                    throw new FileException("Incorrect input format: " + line);
                }

                try {
                    int id = Integer.parseInt(tokens[0]);
                    int year = Integer.parseInt(tokens[3]);
                    Car c = new Car(id, tokens[1], tokens[2], year);
                    this.validator.validateCar(c);
                    elements.put(c.getId(), c);
                } catch (ValidatorException e) {
                    throw new ValidatorException("Error reading from binary file. Invalid car: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new FileException("Error reading from file: " + filename + " - " + e.getMessage());
        }
    }

    @Override
    protected void writeToFile() throws FileException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            Iterator<Car> it = getIterator();
            while (it.hasNext()) {
                Car c = it.next();
                bw.write(c.getId() + "," + c.getBrand() + "," + c.getModel() + "," + c.getYear() + '\n');
            }
        } catch (IOException e) {
            throw new FileException("Error writing to file: " + filename + e.getMessage());
        }
    }
}
