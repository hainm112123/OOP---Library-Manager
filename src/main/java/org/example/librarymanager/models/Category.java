package org.example.librarymanager.models;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category implements Model {
    private int id;
    private String name;
    private String description;

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Category(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.description = rs.getString("description");
    }

    @Override
    public String toString() {
        return "Id: " + id + "\n"
                + "Name: " + name + "\n"
                + "Description: " + description + "\n";
    }

    @Override
    public Model clone() {
        try {
            return (Category) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<String> getAttributes() {
        return List.of("ID", "Name", "Description");
    }

    @Override
    public List<Pair<String, String>> getData() {
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(new Pair<>("ID", String.valueOf(this.id)));
        list.add(new Pair<>("Name", (this.name == null ? "" : this.name) ));
        list.add(new Pair<>("Description", (this.description == null ? "" : this.description) ));
        return list;
    }

    @Override
    public void setData(List<Pair<String, String>> data) {
        if (data == null) {
            this.id = 0;
            this.name = "";
            this.description = "";
        } else {
            this.id = Integer.parseInt(data.get(0).getValue());
            this.name = (String) data.get(1).getValue();
            this.description = (String) data.get(2).getValue();
        }
    }
}
