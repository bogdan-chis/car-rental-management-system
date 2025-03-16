package filters;

import domain.Reservation;

import java.util.logging.Filter;

public class FilterReservationsByCarModel implements AbstractFilter<Reservation> {
    private final String model;

    public FilterReservationsByCarModel(String model){
        this.model = model;
    }

    @Override
    public boolean accept(Reservation r) {
        return r.getCar().getModel().equalsIgnoreCase(model);
    }
}
