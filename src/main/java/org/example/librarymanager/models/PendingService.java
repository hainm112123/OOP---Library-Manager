package org.example.librarymanager.models;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class PendingService {
    private int userId;
    private int documentId;
    private String username;
    private String userAvatar;
    private String documentTitle;
    private String documentCategory;
    private String documentImage;

    public PendingService(ResultSet rs) throws SQLException {
        this.userId = rs.getInt("userId");
        this.documentId = rs.getInt("documentId");
        this.username = rs.getString("username");
        this.userAvatar = rs.getString("userAvatar");
        this.documentTitle = rs.getString("documentTitle");
        this.documentCategory = rs.getString("documentCategory");
        this.documentImage = rs.getString("documentImage");
    }
}
