package org.example.librarymanager.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class categoryModal {
    private int id;
    private String name;
    private String description;

    public categoryModal(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
