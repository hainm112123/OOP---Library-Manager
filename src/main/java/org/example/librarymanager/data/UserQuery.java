package org.example.librarymanager.data;

import org.example.librarymanager.models.User;

import java.sql.*;
import java.util.List;

public class UserQuery implements DataAccessObject<User> {
    private static UserQuery instance;
    private DatabaseConnection databaseConnection;

    private UserQuery(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public static UserQuery getInstance() {
        if (instance == null) {
            instance = new UserQuery(DatabaseConnection.getInstance());
        }
        return instance;
    }

    @Override
    public List<User> getAll() {
        return List.of();
    }

    /**
     * Get a user by id in database.
     */
    @Override
    public User getById(int id) {
        User user = null;
        try (Connection connection = databaseConnection.getConnection();) {
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

    @Override
    public User add(User user) {
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement createUserSt = connection.prepareStatement("insert into users (username, password, firstname, lastname, gender, dateOfBirth) values (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            createUserSt.setString(1, user.getUsername());
            createUserSt.setString(2, user.getPassword());
            createUserSt.setString(3, user.getFirstname());
            createUserSt.setString(4, user.getLastname());
            createUserSt.setString(5, user.getGender());
            createUserSt.setDate(6, Date.valueOf(user.getDateOfBirth()));
            createUserSt.executeUpdate();
            ResultSet generatedKeys = createUserSt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                return UserQuery.getInstance().getById(userId);
            }
            generatedKeys.close();
            createUserSt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(User entity) {
        return false;
    }

    @Override
    public boolean delete(User entity) {
        return false;
    }
}
