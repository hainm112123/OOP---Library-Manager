package org.example.librarymanager.data;

import org.example.librarymanager.models.User;

import java.sql.*;
import java.time.LocalDate;

public class AuthQuery {
    public static AuthResult login(String username, String password) {
        AuthResult result = new AuthResult();
        result.setMessage("Login failed!");
        try (Connection connection = DatabaseConnection.getConnection();) {
            PreparedStatement ps = connection.prepareStatement("select * from users where username = ? and password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                 result.setUser(new User(rs));
                 result.setMessage("Login successful!");
            } else {
                result.setMessage("Wrong username or password!");
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static AuthResult register(String username, String password, String retypePassword, String firstname, String lastname, String gender, LocalDate dateOfBirth) {
        AuthResult result = new AuthResult();
        result.setMessage("register failed!");
        if (!retypePassword.equals(password)) {
            result.setMessage("Retyped password do not match!");
            return result;
        }
        if (username.length() < 4 || username.length() > 16) {
            result.setMessage("Username must be between 4 and 16 characters!");
            return result;
        }
        if (password.length() < 6 || password.length() > 16) {
            result.setMessage("Password must be between 6 and 16 characters!");
            return result;
        }
        if (firstname.isEmpty() || firstname.length() > 30) {
            result.setMessage("Firstname must be between 1 and 30!");
            return result;
        }
        if (lastname.isEmpty() || lastname.length() > 30) {
            result.setMessage("Lastname must be between 1 and 30!");
            return result;
        }
        if (gender.isEmpty()) {
            result.setMessage("Invalid gender!");
            return result;
        }
        if (dateOfBirth == null) {
            result.setMessage("Invalid date of birth!");
            return result;
        }
        try (Connection connection = DatabaseConnection.getConnection();) {
            PreparedStatement ps = connection.prepareStatement("select * from users where username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result.setMessage("Username already exists!");
            } else {
                PreparedStatement createUserSt = connection.prepareStatement("insert into users (username, password, firstname, lastname, gender, dateOfBirth) values (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                createUserSt.setString(1, username);
                createUserSt.setString(2, password);
                createUserSt.setString(3, firstname);
                createUserSt.setString(4, lastname);
                createUserSt.setString(5, gender);
                createUserSt.setDate(6, Date.valueOf(dateOfBirth));
                createUserSt.executeUpdate();
                ResultSet generatedKeys = createUserSt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    result.setMessage("Registration successful!");
                    int userId = generatedKeys.getInt(1);
                    result.setUser(UserQuery.getUserById(userId));
                }
                generatedKeys.close();
                createUserSt.close();
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
