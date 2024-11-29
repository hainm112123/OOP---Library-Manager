package org.example.librarymanager.models;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Data
public class Notification {
    public enum STATUS {
        READ,
        UNREAD
    }

    public enum TYPE {
        REQUEST_APPROVED,
        REQUEST_DECLINED
    }

    private int id;
    private int userId;
    private int documentId;
    private int type;
    private int status;
    private LocalDateTime dateTime;
    private Document document;

    public Notification(int userId, int documentId, int type) {
        this.userId = userId;
        this.documentId = documentId;
        this.type = type;
        this.status = STATUS.UNREAD.ordinal();
    }

    public Notification(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.userId = rs.getInt("userId");
        this.documentId = rs.getInt("documentId");
        this.type = rs.getInt("type");
        this.status = rs.getInt("status");
        this.dateTime = rs.getTimestamp("dateTime").toLocalDateTime();
    }
}
