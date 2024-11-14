package org.example.librarymanager.models;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Data
public class ServiceData {
    private int userId;
    private int count;
    private LocalDate date;

    public ServiceData(ResultSet rs) throws SQLException {
        this.userId = rs.getInt("userId");
        this.count = rs.getInt("count");
        this.date = LocalDate.parse(rs.getString("date"));
    }
}
