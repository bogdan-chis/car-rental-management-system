package repository.database;

import domain.Car;
import domain.Reservation;
import exceptions.ReservationException;
import repository.base.IRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ReservationRepositoryDB implements IRepository<Integer, Reservation> {
    private final String url;

    public ReservationRepositoryDB(String url) {
        this.url = url;
    }

    @Override
    public void add(Integer Rid, Reservation r) {
        String sql = "INSERT INTO reservations(rid, cid, brand, model, year, startDate, endDate) VALUES(?,?,?,?,?,?,?)";
        try(Connection connection = DriverManager.getConnection(url)){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, Rid);
            preparedStatement.setInt(2, r.getCar().getId());
            preparedStatement.setString(3, r.getCar().getBrand());
            preparedStatement.setString(4, r.getCar().getModel());
            preparedStatement.setInt(5,r.getCar().getYear());
            preparedStatement.setString(6, r.getStartDate().toString());
            preparedStatement.setString(7, r.getEndDate().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ReservationException("SQL error while adding: " + e.getMessage());
        }
    }

    @Override
    public Optional<Reservation> delete(Integer id) {
        String findSQL = "SELECT * FROM reservations WHERE rid = ?";
        String deleteSQL = "DELETE FROM reservations WHERE rid = ?";

        try (Connection connection = DriverManager.getConnection(url)) {
            try (PreparedStatement findStmt = connection.prepareStatement(findSQL)) {
                findStmt.setInt(1, id);
                ResultSet rs = findStmt.executeQuery();
                if (rs.next()) {
                    Reservation reservationToDelete = new Reservation(
                            rs.getInt("rid"),
                            new Car(
                                    rs.getInt("cid"),
                                    rs.getString("brand"),
                                    rs.getString("model"),
                                    rs.getInt("year")
                            ),
                            rs.getString("startDate"),
                            rs.getString("endDate")
                    );

                    try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSQL)) {
                        deleteStmt.setInt(1, id);
                        deleteStmt.executeUpdate();
                    }
                    return Optional.of(reservationToDelete);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL error while deleting reservation: " + e.getMessage(), e);
        }
        return Optional.empty();
    }


    @Override
    public void modify(Integer id, Reservation r) {
        String sql = "UPDATE reservations SET cid = ?, brand = ?, " +
                "model = ?, year = ?, startDate = ?, endDate = ? WHERE rid = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, r.getCar().getId());
            pstmt.setString(2, r.getCar().getBrand());
            pstmt.setString(3, r.getCar().getModel());
            pstmt.setInt(4, r.getCar().getYear());
            pstmt.setString(5, r.getStartDate().toString());
            pstmt.setString(6, r.getEndDate().toString());
            pstmt.setInt(7, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new ReservationException("SQL error while modifying: " + e.getMessage());
        }
    }

    @Override
    public Optional<Reservation> findById(Integer id) {
        String sql = "SELECT * FROM reservations WHERE rid = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Reservation(rs.getInt(1),
                        new Car(rs.getInt(2), rs.getString(3), rs.getString(4), rs.getInt(5)),
                        LocalDate.parse(rs.getString(6)),
                        LocalDate.parse(rs.getString(7))));
            }
        } catch (SQLException e) {
            throw new ReservationException("SQL error while finding ID: " + e.getMessage());
        }
        // HOW DO I TEST THIS??
        return Optional.empty(); // or throw an exception if not found
    }

    @Override
    public Iterable<Reservation> getAll() {
        try (Connection conn = DriverManager.getConnection(url)){
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM reservations");

            List<Reservation> reservations = new ArrayList<>();
            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getInt(1),
                        new Car(rs.getInt(2), rs.getString(3), rs.getString(4), rs.getInt(5)),
                        LocalDate.parse(rs.getString(6)),
                        LocalDate.parse(rs.getString(7)));
                reservations.add(reservation);
            }
            return reservations;
        } catch (SQLException e) {
            throw new ReservationException("SQL error while getting all: " + e.getMessage());
        }
    }

    @Override
    public Iterator<Reservation> getIterator() {
        return getAll().iterator();
    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(*) FROM reservations";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new ReservationException("SQL error while getting size: " + e.getMessage());
        }
        return 0;
    }

    public String getUrl(){
        return url;
    }
}
