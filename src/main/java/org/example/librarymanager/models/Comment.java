package org.example.librarymanager.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private int id;
    private int userId;
    private int documentId;
    private String content;
    private LocalDateTime postedTime;

    public Comment(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.userId = rs.getInt("userId");
        this.documentId = rs.getInt("documentId");
        this.content = rs.getString("content");
        this.postedTime = rs.getTimestamp("postedTime").toLocalDateTime();
    }
}
