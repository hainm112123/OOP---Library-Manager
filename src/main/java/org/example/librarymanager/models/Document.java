package org.example.librarymanager.models;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Document implements Model {
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
    private String ownerName;

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
        try {
            this.ownerName = rs.getString("ownerName");
        } catch (Exception e) {
            this.ownerName = "";
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

    @Override
    public Model clone() {
        try {
            return (Document) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<String> getAttributes() {
        return List.of("id", "categoryName", "ownerName", "author", "title", "description",
                "imageLink", "quantity", "quantityInStock", "borrowedTimes", "addDate", "rating" );
    }

    @Override
    public List<Pair<String, String>> getData() {
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(new Pair<>("id", String.valueOf(this.id)));
        list.add(new Pair<>("categoryName", String.valueOf(this.categoryName)));
        list.add(new Pair<>("ownerName", String.valueOf(this.ownerName)));
        list.add(new Pair<>("author", String.valueOf(this.author)));
        list.add(new Pair<>("title", String.valueOf(this.title)));
        list.add(new Pair<>("description", String.valueOf(this.description)));
        list.add(new Pair<>("imageLink", String.valueOf(this.imageLink)));
        list.add(new Pair<>("quantity", String.valueOf(this.quantity)));
        list.add(new Pair<>("quantityInStock", String.valueOf(this.quantityInStock)));
        list.add(new Pair<>("borrowedTimes", String.valueOf(this.borrowedTimes)));
        list.add(new Pair<>("addDate", String.valueOf(this.addDate)));
        list.add(new Pair<>("rating", String.valueOf(this.rating)));
        return list;
    }

    @Override
    public void setData(List<Pair<String, String>> data) {
        if (data == null) {
            this.id = 0;
            this.categoryName = null;
            this.ownerName = null;
            this.author = null;
            this.title = null;
            this.description = null;
            this.imageLink = null;
            this.quantity = 0;
            this.quantityInStock = 0;
            this.borrowedTimes = 0;
            this.addDate = null;
            this.rating = 0.0;
        } else {
            this.id = Integer.parseInt(data.get(0).getValue());
            this.categoryName = data.get(1).getValue();
            this.ownerName = data.get(2).getValue();
            this.author = data.get(3).getValue();
            this.title = data.get(4).getValue();
            this.description = data.get(5).getValue();
            this.imageLink = data.get(6).getValue();
            this.quantity = Integer.parseInt(data.get(7).getValue());
            this.quantityInStock = Integer.parseInt(data.get(8).getValue());
            this.borrowedTimes = Integer.parseInt(data.get(9).getValue());
            this.addDate = LocalDate.parse(data.get(10).getValue());
            this.rating = Double.parseDouble(data.get(11).getValue());
        }
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
