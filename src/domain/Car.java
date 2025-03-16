package domain;

import java.io.Serializable;
import java.util.Objects;

public class Car implements Identifiable<Integer>, Serializable {
    private Integer id;
    private String brand;
    private String model;
    private int year;

    public Car(){

    }

    public Car(Integer id, String brand, String model, int year) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    public Car(int cid, String brand, String model, String year) {
        this.id = cid;
        this.brand = brand;
        this.model = model;
        this.year = Integer.parseInt(year);
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Car [id=" + id + ", brand=" + brand + ", model=" + model + ", year=" + year + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return id == car.id && year == car.year && Objects.equals(brand, car.brand) && Objects.equals(model, car.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, brand, model, year);
    }
}
