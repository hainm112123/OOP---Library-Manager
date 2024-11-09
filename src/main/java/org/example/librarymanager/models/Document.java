package org.example.librarymanager.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;

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
    private double rating;
    private String categoryName;

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
            this.rating = rs.getDouble("rating");
        } catch (Exception e) {
            this.rating = -1;
        }
        try {
            this.categoryName = rs.getString("categoryName");
        } catch (Exception e) {
            this.categoryName = "";
        }
    }

    public Document(org.apache.lucene.document.Document luceneDocument) {
        this.id = Integer.parseInt(luceneDocument.get("id"));
        this.categoryId = Integer.parseInt(luceneDocument.get("categoryId"));
        this.owner = Integer.parseInt(luceneDocument.get("owner"));
        this.author = luceneDocument.get("author");
        this.title = luceneDocument.get("title");
        this.description = luceneDocument.get("description");
        this.imageLink = luceneDocument.get("imageLink");
        this.quantity = Integer.parseInt(luceneDocument.get("quantity"));
        this.quantityInStock = Integer.parseInt(luceneDocument.get("quantityInStock"));
        this.borrowedTimes = Integer.parseInt(luceneDocument.get("borrowedTimes"));
        this.addDate = LocalDate.parse(luceneDocument.get("addDate"));
        this.rating = Double.parseDouble(luceneDocument.get("rating"));
        this.categoryName = luceneDocument.get("categoryName");
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

    public static class SortByTitle implements Comparator<Document> {
        public int compare(Document o1, Document o2) {
            return o1.title.compareToIgnoreCase(o2.title);
        }
    }

    public static class SortByRate implements Comparator<Document> {
        public int compare(Document o1, Document o2) {
            return Double.compare(o1.rating, o2.rating);
        }
    }

    public static class SortByDate implements Comparator<Document> {
        public int compare(Document o1, Document o2) {
            return o1.addDate.compareTo(o2.addDate);
        }
    }

    public static class SortByBorrowed implements Comparator<Document> {
        public int compare(Document o1, Document o2) {
            return Integer.compare(o1.borrowedTimes, o2.borrowedTimes);
        }
    }
}
