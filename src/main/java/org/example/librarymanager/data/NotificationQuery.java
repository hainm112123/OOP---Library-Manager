package org.example.librarymanager.data;

import org.example.librarymanager.models.Notification;
import software.amazon.awssdk.services.s3.endpoints.internal.Not;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NotificationQuery implements DataAccessObject<Notification> {
    private static NotificationQuery instance;
    private DatabaseConnection databaseConnection;

    public NotificationQuery(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public static NotificationQuery getInstance() {
        if (instance == null) {
            instance = new NotificationQuery(DatabaseConnection.getInstance());
        }
        return instance;
    }

    @Override
    public List<Notification> getAll() {
        return List.of();
    }

    @Override
    public Notification getById(int id) {
        return null;
    }

    @Override
    public Notification add(Notification notification) {
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("insert into notifications (userId, documentId, type, status) values(?, ?, ?, ?)");
            ps.setInt(1, notification.getUserId());
            ps.setInt(2, notification.getDocumentId());
            ps.setInt(3, notification.getType());
            ps.setInt(4, notification.getStatus());
            ps.executeUpdate();
            ps.close();
            return notification;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Notification notification) {
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("update notifications set status = ?, type = ? where id = ?");
            ps.setInt(1, notification.getStatus());
            ps.setInt(2, notification.getType());
            ps.setInt(3, notification.getId());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Notification entity) {
        return false;
    }

    /**
     * unread notifications
     * @param userId
     * @return
     */
    public List<Notification> getUnreadNotifications(int userId) {
        List<Notification> notifications = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("select * from notifications where userId = ? and status = ? order by dateTime desc");
            ps.setInt(1, userId);
            ps.setInt(2, Notification.STATUS.UNREAD.ordinal());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Notification notification = new Notification(rs);
                notification.setDocument(DocumentQuery.getInstance().getById(notification.getDocumentId()));
                notifications.add(notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notifications;
    }
}
