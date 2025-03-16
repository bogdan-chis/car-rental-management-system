package filters;

import domain.Car;

public class FilterCarsByYear implements AbstractFilter<Car>{
    int year;

    public FilterCarsByYear(int year){
        this.year = year;
    }

    @Override
    public boolean accept(Car car){
        return car.getYear() == year;
    }
}
