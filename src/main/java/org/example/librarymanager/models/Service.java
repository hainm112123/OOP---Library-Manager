package org.example.librarymanager.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Service {
    private int id;
    private int userId;
    private int documentId;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    public Service(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.userId = rs.getInt("userId");
        this.documentId = rs.getInt("documentId");
        this.borrowDate = LocalDate.parse(rs.getString("borrowDate"));
        String date = rs.getString("returnDate");
        this.returnDate = date != null ? LocalDate.parse(date) : null;
    }
}
