package repository.file.text;

import domain.Car;
import domain.Reservation;
import exceptions.FileException;
import exceptions.ValidatorException;
import repository.file.FileRepository;
import validators.ReservationValidator;

import java.io.*;
import java.time.LocalDate;
import java.util.Iterator;

public class ReservationRepositoryTextFile extends FileRepository<Integer, Reservation> {
    ReservationValidator validator = new ReservationValidator();

    public ReservationRepositoryTextFile(String filename) throws FileException {
        super(filename);
        readFromFile();
    }

    @Override
    protected void readFromFile() throws FileException, ValidatorException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename)))
        {
            String line;
            while((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if(tokens.length != 7){
                    throw new FileException("Incorrect input format:" + line);
                }
                else{
                    try {
                        int id = Integer.parseInt(tokens[0]);
                        //                                  ID          Brand   Model           Year
                        Car car = new Car(Integer.parseInt(tokens[1]), tokens[2], tokens[3], Integer.parseInt(tokens[4]));
                        LocalDate startDate = LocalDate.parse(tokens[5]);
                        LocalDate endDate = LocalDate.parse(tokens[6]);
                        Reservation reservation = new Reservation(id, car, startDate, endDate);

                        validator.validateReservation(reservation);
                        elements.put(id, reservation);
                    } catch (ValidatorException e) {
                        throw new ValidatorException("Invalid reservation: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new FileException("Error reading from file: " + filename + " - " + e.getMessage());
        }
    }

    @Override
    protected void writeToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.filename))) {
            Iterator<Reservation> it = getIterator();
            while (it.hasNext()) {
                Reservation r = it.next();
                bw.write(r.getId() + "," + r.getCar().getId() + "," + r.getCar().getBrand() + "," + r.getCar().getModel() + "," + r.getCar().getYear() + "," + r.getStartDate() + ',' + r.getEndDate() + '\n');
            }
        }
        catch (IOException e) {
            throw new FileException("Error writing to file: " + filename + e.getMessage());
        }
    }
}
