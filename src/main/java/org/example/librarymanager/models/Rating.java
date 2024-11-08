package org.example.librarymanager.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    private int id;
    private int userId;
    private int documentId;
    private double value;
    private String content;
    private LocalDateTime postedTime;

    public Rating(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.userId = rs.getInt("userId");
        this.documentId = rs.getInt("documentId");
        this.value = rs.getDouble("value");
        this.content = rs.getString("content");
        this.postedTime = rs.getTimestamp("postedTime").toLocalDateTime();
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
}