package filters;

import domain.Car;
import domain.Reservation;

public class FilterReservationsByCar implements AbstractFilter<Reservation> {
    private final Car car;

    public FilterReservationsByCar(Car car){
        this.car = car;
    }

    @Override
    public boolean accept(Reservation r){
        return r.getCar().equals(car);
    }

}
