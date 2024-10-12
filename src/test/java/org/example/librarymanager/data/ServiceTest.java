package org.example.librarymanager.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceTest {
    @Test
    public void testBorrowDocument() {
        int userId = 2;
        int documentId = 3;
        Assertions.assertTrue(ServiceQuery.borrowDocument(userId, documentId));
    }

    @Test
    public void testReturnDocument() {
        int userId = 1;
        int documentId = 2;
        Assertions.assertTrue(ServiceQuery.returnDocument(userId, documentId));
    }
}
