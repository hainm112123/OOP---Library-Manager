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
public class Document {
    private int id;
    private int categoryId;
    private int author;
    private String title;
    private String description;
    private String content;
    private int quantity;
    private int quantityInStock;
    private int borrowedTimes;
    private LocalDate addDate;

    public Document(int categoryId, int author, String title, String description, String content, int quantity, int quantityInStock, int borrowedTimes, LocalDate addDate) {
        this.categoryId = categoryId;
        this.author = author;
        this.title = title;
        this.description = description;
        this.content = content;
        this.quantity = quantity;
        this.quantityInStock = quantityInStock;
        this.borrowedTimes = borrowedTimes;
        this.addDate = addDate;
    }

    public Document(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.categoryId = rs.getInt("category_id");
        this.author = rs.getInt("author");
        this.title = rs.getString("title");
        this.description = rs.getString("description");
        this.content = rs.getString("content");
        this.quantity = rs.getInt("quantity");
        this.quantityInStock = rs.getInt("quantityInStock");
        this.borrowedTimes = rs.getInt("borrowedTimes");
        this.addDate = LocalDate.parse(rs.getString("addDate"));
    }
}
