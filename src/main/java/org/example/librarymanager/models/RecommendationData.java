package org.example.librarymanager.models;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class RecommendationData {
    private int userId;
    private int documentId;
    private double preference;

    public RecommendationData(ResultSet rs) throws SQLException {
        this.userId = rs.getInt("userId");
        this.documentId = rs.getInt("documentId");
        this.preference = rs.getDouble("preference");
    }

    @Override
    public String toString() {
        return "userId=" + userId + ", documentId=" + documentId + ", preference=" + preference + "\n";
    }
}
