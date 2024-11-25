package org.example.librarymanager.models;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Service implements Model {
    public static final int STATUS_READING = 0;
    public static final int STATUS_WISH_LIST = 1;
    public static final int STATUS_COMPLETED = 2;
  
    private int id;
    private int userId;
    private int documentId;
    private int status;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private String borrowerName;
    private String documentName;

    public Service(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.userId = rs.getInt("userId");
        this.documentId = rs.getInt("documentId");
        this.status = rs.getInt("status");
        String borrowDateStr = rs.getString("borrowDate");
        this.borrowDate = borrowDateStr != null ? LocalDate.parse(rs.getString("borrowDate")) : null;
        String date = rs.getString("returnDate");
        this.returnDate = date != null ? LocalDate.parse(date) : null;
        try {
            borrowerName = rs.getString("borrowerName");
        } catch (Exception e) {
            borrowerName = "";
        }

        try {
            documentName = rs.getString("documentName");
        } catch (Exception e) {
            documentName = "";
        }
    }

    @Override
    public String toString() {
        return "Id: " + id + "\n"
                + "UserId: " + userId + "\n"
                + "DocumentId: " + documentId + "\n"
                + "BorrowDate: " + borrowDate + "\n"
                + "ReturnDate: " + returnDate + "\n";
    }

    @Override
    public Model clone() {
        try {
            return (Service) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<String> getAttributes() {
        return List.of("id", "borrowerName", "documentName", "borrowDate", "returnDate");
    }

    @Override
    public List<Pair<String, String>> getData() {
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(new Pair<>("id", String.valueOf(id)));
        list.add(new Pair<>("borrowerName", (borrowerName == null ? "" : borrowerName)));
        list.add(new Pair<>("documentName", (documentName == null ? "" : documentName)));
        list.add(new Pair<>("borrowDate", (borrowDate == null ? "" : borrowDate.toString())));
        list.add(new Pair<>("returnDate", (returnDate == null ? "" : returnDate.toString())));
        return list;
    }

    @Override
    public void setData(List<Pair<String, String>> data) {
        this.id = Integer.parseInt(data.get(0).getValue());
        this.borrowerName = data.get(1).getValue();
        this.documentName = data.get(2).getValue();
        this.borrowDate = LocalDate.parse(data.get(3).getValue());
        this.returnDate = LocalDate.parse(data.get(4).getValue());
    }
}
