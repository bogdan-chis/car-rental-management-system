package domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Reservation implements Identifiable<Integer>, Serializable {
    private Integer id;
    private Car car;
    private LocalDate startDate;
    private LocalDate endDate;

    public Reservation(){

    }

    public Reservation(Integer id, Car car, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.car = car;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Reservation(int rid, Car car, String startDate, String endDate) {
        this.id = rid;
        this.car =car;
        this.startDate = LocalDate.parse(startDate);
        this.endDate = LocalDate.parse(endDate);
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Reservation [id=" + id + ", car=" + car + ", startDate=" + startDate + ", endDate=" + endDate + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id) && Objects.equals(car, that.car) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, car, startDate, endDate);
    }
}
