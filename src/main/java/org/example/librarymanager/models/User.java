package org.example.librarymanager.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    public static final int TYPE_USER = 0;
    public static final int TYPE_MODERATOR = 1;
    public static final int TYPE_ADMIN = 2;
    public static final String[] USER_TYPE_STRING = {"User", "Moderator", "Admin"};

    private int id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String gender;
    private LocalDate dateOfBirth;
    private int permission;

    public User(String username, String password, String firstname, String lastname, String gender, LocalDate dateOfBirth) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    public User(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.username = rs.getString("username");
        this.password = rs.getString("password");
        this.firstname = rs.getString("firstname");
        this.lastname = rs.getString("lastname");
        this.gender = rs.getString("gender");
        this.dateOfBirth = new Date(rs.getDate("dateOfBirth").getTime()).toLocalDate();
        this.permission = rs.getInt("permission");
    }
}
