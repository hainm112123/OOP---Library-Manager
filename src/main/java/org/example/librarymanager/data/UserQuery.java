package org.example.librarymanager.data;

import org.example.librarymanager.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserQuery {

    /**
     * Get a user by id in database.
     */
    public static User getUserById(int id) {
        User user = null;
        try (Connection connection = DatabaseConnection.getConnection();) {
            PreparedStatement ps = connection.prepareStatement("select * from users where id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User(rs);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
