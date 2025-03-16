package filters;

import domain.Reservation;

import java.time.LocalDate;

public class FilterReservationsByED implements AbstractFilter<Reservation> {
    LocalDate endDate;

    public FilterReservationsByED(LocalDate endDate){
        this.endDate = endDate;
    }

    @Override
    public boolean accept(Reservation r){
        return r.getEndDate().equals(endDate);
    }
}
