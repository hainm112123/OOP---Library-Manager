package org.example.librarymanager.data;

import org.apache.commons.validator.routines.EmailValidator;
import org.checkerframework.checker.units.qual.A;
import org.example.librarymanager.Common;
import org.example.librarymanager.models.User;

import java.sql.*;
import java.time.LocalDate;

public class AuthQuery {
    private static AuthQuery instance;
    private DatabaseConnection databaseConnection;

    private AuthQuery(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public static synchronized AuthQuery getInstance() {
        if (instance == null) {
            instance = new AuthQuery(DatabaseConnection.getInstance());
        }
        return instance;
    }

    /**
     * Check and execute login query.
     * @return result statement
     */
    public AuthResult login(String email, String password) {
        AuthResult result = new AuthResult();
        result.setMessage("Login failed!");
        try (Connection connection = databaseConnection.getConnection();) {
            PreparedStatement ps = connection.prepareStatement("select * from users where email = ? and password = ?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                 result.setUser(new User(rs));
                 result.setMessage("Login successful!");
            } else {
                result.setMessage("Wrong email or password!");
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Check and execute register query.
     * @return result statement
     */
    public AuthResult register(String email, String username, String password, String retypePassword, String firstname, String lastname, String gender, LocalDate dateOfBirth) {
        AuthResult result = new AuthResult();
        result.setMessage("register failed!");
        if (!retypePassword.equals(password)) {
            result.setMessage("Retyped password do not match!");
            return result;
        }
        if (username.length() < 4 || username.length() > 30) {
            result.setMessage("Username must be between 4 and 30 characters!");
            return result;
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            result.setMessage("Invalid email address!");
            return result;
        }
        if (password.length() < 6 || password.length() > 16) {
            result.setMessage("Password must be between 6 and 16 characters!");
            return result;
        }
        if (firstname.isEmpty() || firstname.length() > 30) {
            result.setMessage("Firstname must be between 1 and 30 characters!");
            return result;
        }
        if (lastname.isEmpty() || lastname.length() > 30) {
            result.setMessage("Lastname must be between 1 and 30 characters!");
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
        try (Connection connection = databaseConnection.getConnection();) {
            PreparedStatement ps = connection.prepareStatement("select * from users where email = ?");
            PreparedStatement ps2 = connection.prepareStatement("select * from users where username = ?");
            ps.setString(1, email);
            ps2.setString(1, username);
            ResultSet rs = ps.executeQuery();
            ResultSet rs2 = ps2.executeQuery();
            if (rs.next()) {
                result.setMessage("Email already exists!");
            } else if (rs2.next()) {
                result.setMessage("Username already exists!");
            } else {
                User user = UserQuery.getInstance().add(new User(email, username, password, firstname, lastname, gender, dateOfBirth));
                if (user != null) {
                    result.setMessage("Registration successful!");
                    result.setUser(user);
                }
            }
            ps.close();
            ps2.close();
            rs.close();
            rs2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public AuthResult changePassword(User user, String currentPassword, String newPassword, String retypePassword) {
        AuthResult result = new AuthResult();
        if (!user.getPassword().equals(currentPassword)) {
            result.setMessage("Wrong password!");
            return result;
        }
        if (!newPassword.equals(retypePassword)) {
            result.setMessage("Retyped password does not match!");
            return result;
        }
        if (newPassword.length() < 6 || newPassword.length() > 16) {
            result.setMessage("Password must be between 6 and 16 characters!");
            return result;
        }
        user.setPassword(newPassword);
        if (UserQuery.getInstance().update(user)) {
            result.setMessage("Password successfully changed!");
            result.setUser(user);
        } else {
            result.setMessage("Some errors occur, please try again!");
            user.setPassword(currentPassword);
        }
        return result;
    }

    public AuthResult registerWithGoogle(String email, String firstName, String lastName, String imageLink) {
        AuthResult result = new AuthResult();
        result.setMessage("register failed!");
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("select * from users where email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result.setMessage("Email already exists!");
            } else {
                String[] parts = email.split("@");
                String username = parts[0] + "_" + parts[1].split("\\.")[0] + "_" + Common.hashString(email, 6);
                User user = new User();
                user.setEmail(email);
                user.setUsername(username);
                user.setFirstname(firstName);
                user.setLastname(lastName);
                user.setImageLink(imageLink);
                user = UserQuery.getInstance().add(user);
                if (user != null) {
                    result.setMessage("Registration successful!");
                    result.setUser(user);
                }
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
