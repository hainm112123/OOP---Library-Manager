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
    private String email;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String gender;
    private LocalDate dateOfBirth;
    private int permission;
    private String imageLink;

    public User(String email, String username, String password, String firstname, String lastname, String gender, LocalDate dateOfBirth) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    public User(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.email = rs.getString("email");
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
                + "Email: " + email + "\n"
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
        return List.of("ID", "Email", "Username", "Password", "First Name", "Last Name", "Gender", "Date Of Birth", "Permission", "Image Link");
    }

    @Override
    public List<Pair<String, String>> getData() {
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(new Pair<>("ID", String.valueOf(id)));
        list.add(new Pair<>("Email", (email == null ? "" : email)));
        list.add(new Pair<>("Username", (username == null ? "" : username)));
        list.add(new Pair<>("Password", (password == null ? "" : password)));
        list.add(new Pair<>("First Name", (firstname == null ? "" : firstname)));
        list.add(new Pair<>("Last Name", (lastname == null ? "" : lastname)));
        list.add(new Pair<>("Gender", (gender == null ? "" : gender)));
        list.add(new Pair<>("Date Of Birth", (dateOfBirth == null ? "" : dateOfBirth.toString())));
        list.add(new Pair<>("Permission", USER_TYPE_STRING[permission]));
        list.add(new Pair<>("Image Link", (imageLink == null ? "" : imageLink)));
        return list;
    }

    @Override
    public void setData(List<Pair<String, String>> data) {
        if (data == null) {
            this.id = 0;
            this.email = "";
            this.username = "";
            this.password = "";
            this.firstname = "";
            this.lastname = "";
            this.gender = "";
            this.dateOfBirth = null;
            this.permission = 0;
            this.imageLink = "";
        } else {
            this.id = Integer.parseInt(data.get(0).getValue());
            this.email = data.get(1).getValue();
            this.username = data.get(2).getValue();
            this.password = data.get(3).getValue();
            this.firstname = data.get(4).getValue();
            this.lastname = data.get(5).getValue();
            this.gender = data.get(6).getValue();
            this.dateOfBirth = data.get(7).getValue() != null && !data.get(7).getValue().isEmpty() ? LocalDate.parse(data.get(7).getValue()) : null;
            for (int i = 0; i < 3; i++)
                if (data.get(8).getValue().equals(USER_TYPE_STRING[i])) {
                    this.permission = i;
                }
            this.imageLink = data.get(9).getValue();
        }
    }
}
