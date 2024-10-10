package org.example.librarymanager.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private int id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String gender;
    private LocalDate dateOfBirth;

    public UserModel(LocalDate dateOfBirth, String gender, String lastname, String firstname, String password, String username) {
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.lastname = lastname;
        this.firstname = firstname;
        this.password = password;
        this.username = username;
    }
}
