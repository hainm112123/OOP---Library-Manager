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
    public static final int STATUS_READING = 0;
    public static final int STATUS_WISH_LIST = 1;
    public static final int STATUS_COMPLETED = 2;

    private int id;
    private int userId;
    private int documentId;
    private int status;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    public Service(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.userId = rs.getInt("userId");
        this.documentId = rs.getInt("documentId");
        this.status = rs.getInt("status");
        String borrowDateStr = rs.getString("borrowDate");
        this.borrowDate = borrowDateStr != null ? LocalDate.parse(rs.getString("borrowDate")) : null;
        String date = rs.getString("returnDate");
        this.returnDate = date != null ? LocalDate.parse(date) : null;
    }

    @Override
    public String toString() {
        return "Id: " + id + "\n"
                + "UserId: " + userId + "\n"
                + "DocumentId: " + documentId + "\n"
                + "BorrowDate: " + borrowDate + "\n"
                + "ReturnDate: " + returnDate + "\n";
    }
}
