package repository.file.binary;

import domain.Reservation;
import exceptions.FileException;
import exceptions.ValidatorException;
import repository.file.FileRepository;
import validators.ReservationValidator;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ReservationRepositoryBinaryFile extends FileRepository<Integer, Reservation> {
    private final ReservationValidator validator = new ReservationValidator();

    public ReservationRepositoryBinaryFile(String filename) throws FileException, ValidatorException {
        super(filename);
        readFromFile();
    }

    @Override
    protected void readFromFile() throws FileException, ValidatorException {
        try (FileInputStream fis = new FileInputStream(this.filename);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            // Deserialize and cast
            HashMap<Integer, Reservation> deserializedElements;
            try {
                deserializedElements = (HashMap<Integer, Reservation>) ois.readObject();

            } catch (ClassCastException | ClassNotFoundException e) {
                throw new FileException("Error deserializing data from file: " + filename + " - Invalid object type - " + e.getMessage());
            }
            // Validate each deserialized Reservation object
            for (Map.Entry<Integer, Reservation> entry : deserializedElements.entrySet()) {
                try {
                    validator.validateReservation(entry.getValue());
                    this.elements.put(entry.getKey(), entry.getValue()); // Only add valid reservations
                } catch (ValidatorException e) {
                    throw new ValidatorException("Error reading from binary file. Invalid reservation: " + entry.getValue() + " - " + e.getMessage());
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
