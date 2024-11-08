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
    private int owner;
    private String author;
    private String title;
    private String description;
    private String imageLink;
    private int quantity;
    private int quantityInStock;
    private int borrowedTimes;
    private LocalDate addDate;
    private float rating;

    public Document(int categoryId, int owner, String author, String title, String description, String imageLink, int quantity) {
        this.categoryId = categoryId;
        this.owner = owner;
        this.author = author;
        this.title = title;
        this.description = description;
        this.imageLink = imageLink;
        this.quantity = quantity;
    }

    public Document(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.categoryId = rs.getInt("categoryId");
        this.owner = rs.getInt("owner");
        this.author = rs.getString("author");
        this.title = rs.getString("title");
        this.description = rs.getString("description");
        this.imageLink = rs.getString("imageLink");
        this.quantity = rs.getInt("quantity");
        this.quantityInStock = rs.getInt("quantityInStock");
        this.borrowedTimes = rs.getInt("borrowedTimes");
        this.addDate = LocalDate.parse(rs.getString("addDate"));
        try {
            this.rating = rs.getFloat("rating");
        } catch (Exception e) {
            this.rating = -1;
        }
    }

    @Override
    public String toString() {
        return "Id: " + id + "\n"
                + "Category Id: " + categoryId + "\n"
                + "Owner: " + owner + "\n"
                + "Author: " + author + "\n"
                + "Title: " + title + "\n"
                + "Description: " + description + "\n"
                + "Quantity: " + quantity + "\n"
                + "Quantity In Stock: " + quantityInStock + "\n"
                + "Borrowed Times: " + borrowedTimes + "\n"
                + "Rating: " + rating + "\n";
    }
}
