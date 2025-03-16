package filters;

import domain.Reservation;

public class FilterReservationsByCarBrand implements AbstractFilter<Reservation> {

    private final String brand;

    public FilterReservationsByCarBrand(String brand){
        this.brand = brand;
    }

    @Override
    public boolean accept(Reservation r) {
        return r.getCar().getBrand().equalsIgnoreCase(brand);
    }
}
