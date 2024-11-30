package org.example.librarymanager.data;

import org.example.librarymanager.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserQuery implements DataAccessObject<User> {
    private static UserQuery instance;
    private DatabaseConnection databaseConnection;

    private UserQuery(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public static synchronized UserQuery getInstance() {
        if (instance == null) {
            instance = new UserQuery(DatabaseConnection.getInstance());
        }
        return instance;
    }

    /**
     * Count data.
     */
    public int count() {
        int cnt = 0;
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM users;");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cnt = rs.getInt(1);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cnt;
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(new User(rs));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
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
            PreparedStatement createUserSt = connection.prepareStatement("insert into users (email, username, password, firstname, lastname, gender, dateOfBirth, imageLink) values (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            createUserSt.setString(1, user.getEmail());
            createUserSt.setString(2, user.getUsername());
            createUserSt.setString(3, user.getPassword());
            createUserSt.setString(4, user.getFirstname());
            createUserSt.setString(5, user.getLastname());
            createUserSt.setString(6, user.getGender());
            createUserSt.setDate(7, user.getDateOfBirth() != null ? Date.valueOf(user.getDateOfBirth()) : null);
            createUserSt.setString(8, user.getImageLink());
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
    public boolean update(User user) {
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("update users set " +
                    "password = ?, firstname = ?, lastname = ?, gender = ?, dateOfBirth = ?, permission = ?, imageLink = ? " +
                    "where id = ?"
            );
            ps.setString(1, user.getPassword());
            ps.setString(2, user.getFirstname());
            ps.setString(3, user.getLastname());
            ps.setString(4, user.getGender());
            ps.setDate(5, user.getDateOfBirth() != null ? Date.valueOf(user.getDateOfBirth()) : null);
            ps.setInt(6, user.getPermission());
            ps.setString(7, user.getImageLink());
            ps.setInt(8, user.getId());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(User user) {
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("delete from users where id = ?");
            ps.setInt(1, user.getId());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
