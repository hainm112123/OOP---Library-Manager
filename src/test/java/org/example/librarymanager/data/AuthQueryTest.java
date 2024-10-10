package org.example.librarymanager.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class AuthQueryTest {
    @Test
    public void testRegister() {
        String username = "admin3";
        String password = "123456";
        String retypePassword = "123456";
        String firstname = "admin3";
        String lastname = "admin3";
        String gender = "male";
        LocalDate dateOfBirth = LocalDate.of(2005, 2, 13);
        AuthResult result = AuthQuery.register(username, password, retypePassword, firstname, lastname, gender, dateOfBirth);
        Assertions.assertEquals(result.getMessage(), "Registration successful!");
    }

    @Test
    public void testLogin() {
        String username = "admin";
        String password = "123456";
        AuthResult result = AuthQuery.login(username, password);
        Assertions.assertEquals(result.getMessage(), "Login successful!");
    }
}
