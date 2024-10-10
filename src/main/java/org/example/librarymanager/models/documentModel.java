package org.example.librarymanager.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class documentModel {
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

    public documentModel(int categoryId, int author, String title, String description, String content, int quantity, int quantityInStock, int borrowedTimes, LocalDate addDate) {
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
}
