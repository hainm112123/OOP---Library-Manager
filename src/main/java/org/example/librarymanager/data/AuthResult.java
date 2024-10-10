package org.example.librarymanager.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.librarymanager.models.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResult {
    private String message;
    private User user;

    @Override
    public boolean equals(Object o) {
        if (o instanceof AuthResult) {
            AuthResult other = (AuthResult) o;
            return this.message.equals(other.message) && this.user.equals(other.user);
        }
        else {
            return false;
        }
    }
}