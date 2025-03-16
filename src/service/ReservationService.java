package service;

import domain.Car;
import domain.Reservation;
import exceptions.ReservationException;
import exceptions.ValidatorException;
import repository.base.IRepository;
import utils.StreamUtils;
import validators.ReservationValidator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReservationService {
    private IRepository<Integer, Reservation> repository;
    private ReservationValidator validator;

    public ReservationService(IRepository<Integer, Reservation> reservationRepository, ReservationValidator validator) {
        this.repository = reservationRepository;
        this.validator = validator;
    }

    public void add (Reservation r) throws ValidatorException {
        try{
            validator.validateReservation(r);
            if (repository.findById(r.getId()).isPresent())
                throw new ValidatorException("Cannot add a reservation with id = " + r.getId() + ". Already exists a reservation with the same id");
            repository.add(r.getId(), r);
        }
        catch (ReservationException e){
            throw new ValidatorException("\tInvalid reservation: " + e.getMessage());
        }
    }

    public Iterable<Reservation> getAll() throws ValidatorException{
        try{
            Iterable<Reservation> allReservations = repository.getAll();
            if (! allReservations.iterator().hasNext())
                throw new ValidatorException("\tThere are no reservations");
            return allReservations;
        }
        catch(ReservationException e){
            throw new ValidatorException(e.getMessage());
        }
    }

    public List<Reservation> getReservationsList(){
        List<Reservation> reservationList = new ArrayList<>();
        Iterator<Reservation> iterator = repository.getIterator();
        while (iterator.hasNext())
            reservationList.add(iterator.next());
        return reservationList;
    }

    public Reservation delete(int id) throws ValidatorException{
        try{
            validator.validateId(id);
            Optional<Reservation> reservation = repository.findById(id);
            if (reservation.isEmpty())
                throw new ValidatorException("\tInvalid ID. There is no reservation with id = " + id + ".");
            return repository.delete(id).get();
        }
        catch (ReservationException e){
            throw new ValidatorException(e.getMessage());
        }
    }

    public List<Reservation> deleteByCarId(int carId) throws ValidatorException{
        try {
            List<Reservation> reservationsToDelete = StreamUtils.toStream(repository.getAll())
                    .filter(reservation -> reservation.getCar().getId() == carId)
                    .collect(Collectors.toList());

            for (Reservation reservation : reservationsToDelete) {
                repository.delete(reservation.getId());
            }
            return reservationsToDelete;
        } catch (ReservationException e) {
            throw new ValidatorException(e.getMessage());
        }
    }

    public void update(Reservation r) throws ValidatorException{
        try{
            validator.validateReservation(r);
            if (repository.findById(r.getId()).isEmpty())
                throw new ValidatorException("\tInvalid ID. There is no reservation with id = " + r.getId() + ".");
            repository.modify(r.getId(), r);
        }
        catch (ReservationException e){
            throw new ValidatorException(e.getMessage());
        }
    }

    public Reservation findById (int rid){
        try {
            validator.validateId(rid);
            if (repository.findById(rid).isEmpty())
                throw new ValidatorException("\t Invalid id. There is no reservation with id = " + rid);
            return repository.findById(rid).get();
        } catch (ReservationException e) {
            throw new ValidatorException(e.getMessage());
        }
    }

    public List<Reservation> filter (Predicate<Reservation> cond){
        List<Reservation> filteredReservations = new ArrayList<>();
        Iterator<Reservation> iterator = repository.getIterator();

        while(iterator.hasNext()){
            Reservation reservation = iterator.next();
            if (cond.test(reservation))
                filteredReservations.add(reservation);
        }

        return filteredReservations;
    }

    public void updateReservationsForCar(int oldCarId, Car newCar) {
       Iterator<Reservation> iterator = repository.getIterator();

       while (iterator.hasNext()){
           Reservation reservation = iterator.next();
           if (reservation.getCar().getId() == oldCarId) {
               reservation.setCar(newCar);
               repository.modify(reservation.getId(), reservation);
           }
       }
    }
}
