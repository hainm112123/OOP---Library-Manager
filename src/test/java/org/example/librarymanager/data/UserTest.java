package org.example.librarymanager.data;

import org.example.librarymanager.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserTest {
    @Test
    public void testGetUserById() {
        User user = UserQuery.getInstance().getById(1);
        Assertions.assertEquals(user.getId(), 1);
        Assertions.assertEquals(user.getUsername(), "admin");
    }

    @Test
    public void testUpdate() {
        User user = UserQuery.getInstance().getById(1);
        user.setLastname("ultimate");
        Assertions.assertTrue(UserQuery.getInstance().update(user));
    }

    @Test
    public void testDelete() {
        User user = UserQuery.getInstance().getById(1);
        Assertions.assertTrue(UserQuery.getInstance().delete(user));
    }
}
