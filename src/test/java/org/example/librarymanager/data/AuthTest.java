package org.example.librarymanager.data;

import org.apache.commons.validator.routines.EmailValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class AuthTest {
    @Test
    public void testRegister() {
        String username = "admin2";
        String email = "admin2@gmail.com";
        String password = "123456";
        String retypePassword = "123456";
        String firstname = "admin";
        String lastname = "admin";
        String gender = "male";
        LocalDate dateOfBirth = LocalDate.of(2005, 2, 13);
        AuthResult result = AuthQuery.getInstance().register(email, username, password, retypePassword, firstname, lastname, gender, dateOfBirth);
        Assertions.assertEquals(result.getMessage(), "Registration successful!");
    }

    @Test
    public void testLogin() {
        String username = "admin";
        String password = "123456";
        AuthResult result = AuthQuery.getInstance().login(username, password);
        Assertions.assertEquals(result.getMessage(), "Login successful!");
    }

    @Test
    public void testEmailValidator() {
        String email = "abcdfe@gmail.com";
        Assertions.assertTrue(EmailValidator.getInstance().isValid(email));
    }
}
