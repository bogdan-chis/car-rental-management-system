package filters;

import domain.Car;

public class FilterCarsByModel implements AbstractFilter<Car>{
    private String model;

    public FilterCarsByModel(String model){
        this.model = model;
    }

    @Override
    public boolean accept(Car car){
        return car.getModel().equalsIgnoreCase(model);
    }
}
