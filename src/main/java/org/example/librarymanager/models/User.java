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
        if (rs.getDate("dateOfBirth") != null) {
            this.dateOfBirth = new Date(rs.getDate("dateOfBirth").getTime()).toLocalDate();
        }
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
    public User clone() {
        try {
            return (User) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<String> getAttributes() {
        return List.of("id", "username", "password", "firstname", "lastname", "gender", "dateOfBirth", "permission", "imageLink");
    }

    @Override
    public List<Pair<String, String>> getData() {
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(new Pair<>("id", String.valueOf(id)));
        list.add(new Pair<>("username", (username == null ? "" : username)));
        list.add(new Pair<>("password", (password == null ? "" : password)));
        list.add(new Pair<>("firstname", (firstname == null ? "" : firstname)));
        list.add(new Pair<>("lastname", (lastname == null ? "" : lastname)));
        list.add(new Pair<>("gender", (gender == null ? "" : gender)));
        list.add(new Pair<>("dateOfBirth", (dateOfBirth == null ? "" : dateOfBirth.toString())));
        list.add(new Pair<>("permission", USER_TYPE_STRING[permission]));
        list.add(new Pair<>("imageLink", (imageLink == null ? "" : imageLink)));
        return list;
    }

    @Override
    public void setData(List<Pair<String, String>> data) {
        this.id = Integer.parseInt(data.get(0).getValue());
        this.username = data.get(1).getValue();
        this.password = data.get(2).getValue();
        this.firstname = data.get(3).getValue();
        this.lastname = data.get(4).getValue();
        this.gender = data.get(5).getValue();
        this.dateOfBirth = LocalDate.parse(data.get(6).getValue());
        for (int i = 0; i < 3; i++) if (data.get(7).getValue().equals(USER_TYPE_STRING[i])) {
            this.permission = i;
        }
        this.imageLink = data.get(8).getValue();
    }
}
