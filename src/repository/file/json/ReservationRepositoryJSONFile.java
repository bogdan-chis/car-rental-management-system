package repository.file.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import domain.Reservation;
import exceptions.CarException;
import repository.base.IRepository;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


public class ReservationRepositoryJSONFile implements IRepository<Integer, Reservation> {
    private String filePath;
    private ObjectMapper objectMapper;

    public ReservationRepositoryJSONFile(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();

        // Register the JavaTimeModule to handle LocalDate and configure to write dates as arrays
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true); // Enable pretty-printing
        this.objectMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
    }

    private List<Reservation> loadReservations() throws CarException {
        try {
            // Read the JSON file and map it to a List of Reservations
            return objectMapper.readValue(new File(filePath), new TypeReference<List<Reservation>>() {});
        } catch (IOException e) {
            throw new CarException("Error loading reservations from the file: " + e.getMessage());
        }
    }

    private void saveReservations(List<Reservation> reservations) throws CarException {
        try {
            // Write the list of reservations back to the file
            objectMapper.writeValue(new File(filePath), reservations);
        } catch (IOException e) {
            throw new CarException("Error saving reservations to the file: " + e.getMessage());
        }
    }

    @Override
    public void add(Integer id, Reservation reservation) throws CarException {
        try {
            List<Reservation> reservations = loadReservations();

            // Add the reservation with the specified ID
            reservation.setId(id);
            reservations.add(reservation);
            saveReservations(reservations);
        } catch (CarException e) {
            throw new CarException("Error adding reservation : " + e.getMessage());
        }
    }

    @Override
    public Optional<Reservation> delete(Integer id) throws CarException {
        try {
            List<Reservation> reservations = loadReservations();

            Optional<Reservation> reservationToDelete = reservations.stream()
                    .filter(reservation -> reservation.getId().equals(id))
                    .findFirst();

            if (reservationToDelete.isPresent()) {
                reservations.remove(reservationToDelete.get());
                saveReservations(reservations);
            }

            return reservationToDelete;
        } catch (CarException e) {
            throw new CarException("Error deleting reservation: " + e.getMessage());
        }
    }

    @Override
    public void modify(Integer id, Reservation updatedReservation) throws CarException {
        try {
            List<Reservation> reservations = loadReservations();

            // Find and update the reservation
            for (int i = 0; i < reservations.size(); i++) {
                if (reservations.get(i).getId().equals(id)) {
                    reservations.set(i, updatedReservation);
                    break;
                }
            }
            saveReservations(reservations);
        } catch (CarException e) {
            throw new CarException("Error modifying reservation: " + e.getMessage());
        }
    }

    @Override
    public Optional<Reservation> findById(Integer id) throws CarException {
            List<Reservation> reservations = loadReservations();
            return reservations.stream().filter(c -> c.getId().equals(id))
                    .findFirst();
    }

    @Override
    public Iterable<Reservation> getAll() throws CarException {
        try {
            return loadReservations();
        } catch (CarException e) {
            throw new CarException("Error retrieving all reservations: " + e.getMessage());
        }
    }

    @Override
    public Iterator<Reservation> getIterator() throws CarException {
        try {
            return loadReservations().iterator();
        } catch (CarException e) {
            throw new CarException("Error getting reservations iterator: " + e.getMessage());
        }
    }

    @Override
    public int size() throws CarException {
        try {
            return loadReservations().size();
        } catch (CarException e) {
            throw new CarException("Error retrieving reservation size: " + e.getMessage());
        }
    }
}
