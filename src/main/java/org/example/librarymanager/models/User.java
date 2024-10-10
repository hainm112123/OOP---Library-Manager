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
    private int id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String gender;
    private LocalDate dateOfBirth;

    public User(LocalDate dateOfBirth, String gender, String lastname, String firstname, String password, String username) {
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.lastname = lastname;
        this.firstname = firstname;
        this.password = password;
        this.username = username;
    }

    public User(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.username = rs.getString("username");
        this.password = rs.getString("password");
        this.firstname = rs.getString("firstname");
        this.lastname = rs.getString("lastname");
        this.gender = rs.getString("gender");
        this.dateOfBirth = new Date(rs.getDate("dateOfBirth").getTime()).toLocalDate();
    }
}
