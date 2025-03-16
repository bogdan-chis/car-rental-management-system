package filters;

import domain.Car;

public class FilterCarsByBrand implements AbstractFilter<Car>{
    private String brand;

    public FilterCarsByBrand(String brand){
        this.brand = brand;
    }

    public boolean accept(Car car){
        return car.getBrand().equalsIgnoreCase(brand);
    }
}
