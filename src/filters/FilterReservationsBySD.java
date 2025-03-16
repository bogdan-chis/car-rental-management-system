package filters;

import domain.Reservation;

import java.time.LocalDate;

public class FilterReservationsBySD implements AbstractFilter<Reservation> {
    LocalDate startDate;

    public  FilterReservationsBySD (LocalDate startDate){
        this.startDate = startDate;
    }

    @Override
    public boolean accept(Reservation entity) {
        return entity.getStartDate().equals(startDate);
    }
}
