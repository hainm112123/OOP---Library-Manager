package org.example.librarymanager.models;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rating implements Model {
    private int id;
    private int userId;
    private int documentId;
    private double value;
    private String content;
    private LocalDateTime postedTime;
    private String userName;
    private String documentName;

    public Rating(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.userId = rs.getInt("userId");
        this.documentId = rs.getInt("documentId");
        this.value = rs.getDouble("value");
        this.content = rs.getString("content");
        this.postedTime = rs.getTimestamp("postedTime").toLocalDateTime();
        try {
            this.userName = rs.getString("userName");
        } catch (Exception e) {
            this.userName = "";
        }
        try {
            this.documentName = rs.getString("documentName");
        } catch (Exception e) {
            this.documentName = "";
        }
    }

    @Override
    public String toString() {
        return "Id: " + id + "\n"
                + "UserId: " + userId + "\n"
                + "DocumentId: " + documentId + "\n"
                + "Value: " + value + "\n"
                + "Content: " + content + "\n"
                + "PostedTime: " + postedTime + "\n";
    }

    @Override
    public Model clone() {
        try {
            return (Rating) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<String> getAttributes() {
        return List.of("ID", "User", "Document", "Value", "Content", "Posted Time");
    }

    @Override
    public List<Pair<String, String>> getData() {
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(new Pair<>("ID", String.valueOf(id)));
        list.add(new Pair<>("User", (userName == null ? "" : userName)));
        list.add(new Pair<>("Document", (documentName == null ? "" : documentName)));
        list.add(new Pair<>("Value", String.valueOf(value)));
        list.add(new Pair<>("Content", (content == null ? "" : content)));
        list.add(new Pair<>("Posted Time", String.valueOf(postedTime)));
        return list;
    }

    @Override
    public void setData(List<Pair<String, String>> data) {
        if (data == null) {
            this.id = 0;
            this.userId = 0;
            this.documentId = 0;
            this.value = 0.0;
            this.content = null;
            this.postedTime = null;
            this.userName = null;
            this.documentName = null;
        } else {
            this.id = Integer.parseInt(data.get(0).getValue());
            this.userName = data.get(1).getValue();
            this.documentName = data.get(2).getValue();
            this.value = Double.parseDouble(data.get(3).getValue());
            this.content = data.get(4).getValue();
            this.postedTime = LocalDateTime.parse(data.get(5).getValue());
        }
    }
}