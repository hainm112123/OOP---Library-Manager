package org.example.librarymanager.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceTest {
    @Test
    public void testBorrowDocument() {
        int userId = 2;
        int documentId = 13;
        Assertions.assertTrue(ServiceQuery.getInstance().borrowDocument(userId, DocumentQuery.getInstance().getById(documentId)));
    }

    @Test
    public void testReturnDocument() {
        int userId = 2;
        int documentId = 13;
        Assertions.assertTrue(ServiceQuery.getInstance().returnDocument(userId, DocumentQuery.getInstance().getById(documentId)));
    }
}
