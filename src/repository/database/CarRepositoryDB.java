package repository.database;

import domain.Car;
import exceptions.CarException;
import repository.base.IRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class CarRepositoryDB implements IRepository<Integer, Car> {
    private final String url;

    public CarRepositoryDB(String url) {
        this.url = url;
    }

    @Override
    public void add(Integer id, Car car) throws CarException {
        String sql = "INSERT INTO cars(id, brand, model, year) VALUES(?,?,?,?)";
        try(Connection connection = DriverManager.getConnection(url)){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, car.getBrand());
            preparedStatement.setString(3, car.getModel());
            preparedStatement.setInt(4, car.getYear());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new CarException("SQL error while adding: " + e.getMessage());
        }
    }

    @Override
    public Optional<Car> delete(Integer id) throws CarException {
        String findSQL = "SELECT * FROM cars WHERE id = ?";
        String deleteSQL = "DELETE FROM cars WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url)){
            try (PreparedStatement findStmt = connection.prepareStatement(findSQL)) {
                findStmt.setInt(1, id);
                ResultSet rs = findStmt.executeQuery();
                if (rs.next()) {
                    Car carToDelete = new Car(
                            rs.getInt("id"),
                            rs.getString("brand"),
                            rs.getString("model"),
                            rs.getInt("year")
                    );

                    try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSQL)) {
                        deleteStmt.setInt(1, id);
                        deleteStmt.executeUpdate();
                    }
                    return Optional.of(carToDelete);
                }
            }
        } catch (SQLException e) {
            throw new CarException("SQL error while deleting: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void modify(Integer id, Car car) {
        String sql = "UPDATE cars SET brand = ?, model = ?, year = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, car.getBrand());
            pstmt.setString(2, car.getModel());
            pstmt.setInt(3, car.getYear());
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CarException("SQL error while modifying: " + e.getMessage());
        }
    }

    @Override
    public Optional<Car> findById(Integer id) {
        String sql = "SELECT * FROM cars WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Car(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4)));
            }
        } catch (SQLException e) {
            throw new CarException("SQL error while finding ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Car> getAll() {
        try (Connection conn = DriverManager.getConnection(url)){
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM cars");
            List<Car> cars = new ArrayList<>();
            while (rs.next()) {
                Car car = new Car(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new CarException("SQL error while getting all: " + e.getMessage());
        }
    }

    @Override
    public Iterator<Car> getIterator() {
        return getAll().iterator();
    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(*) FROM cars";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             if (rs.next()) {
                return rs.getInt(1);
             }
        } catch (SQLException e) {
            throw new CarException("SQL error while getting size: " + e.getMessage());
        }
        // HOW CAN I TEST THIS?
        return 0;
    }

    public String getUrl()
    {
        return url;
    }
}
