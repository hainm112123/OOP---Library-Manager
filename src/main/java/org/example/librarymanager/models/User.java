package org.example.librarymanager.models;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Model{
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
    private String imageLink;

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
        this.imageLink = rs.getString("imageLink");
    }

    @Override
    public String toString() {
        return "Id: " + id + "\n"
                + "Username: " + username + "\n"
                + "Password: " + password + "\n"
                + "Firstname: " + firstname + "\n"
                + "Lastname: " + lastname + "\n"
                + "Gender: " + gender + "\n"
                + "DateOfBirth: " + dateOfBirth + "\n"
                + "Permission: " + permission + "\n";
    }

    @Override
    public List<Pair<String, String>> getData() {
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(new Pair<>("id", String.valueOf(id)));
        list.add(new Pair<>("username", username));
        list.add(new Pair<>("password", password));
        list.add(new Pair<>("firstname", firstname));
        list.add(new Pair<>("lastname", lastname));
        list.add(new Pair<>("gender", gender));
        list.add(new Pair<>("dateOfBirth", dateOfBirth.toString()));
        list.add(new Pair<>("permission", String.valueOf(permission)));
        list.add(new Pair<>("imageLink", imageLink));
        return list;
    }

    @Override
    public void setData(List<Pair<String, String>> data) {

    }
}
