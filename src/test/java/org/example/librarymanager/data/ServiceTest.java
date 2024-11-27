package org.example.librarymanager.data;

import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.ServiceData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ServiceTest {
    @Test
    public void addToWishlistTest() {
        int userId = 2;
        int documentId = 28;
        Assertions.assertTrue(ServiceQuery.getInstance().addToWishlist(userId, documentId));
    }

    @Test
    public void removeFromWishlistTest() {
        int userId = 2;
        int documentId = 28;
        Assertions.assertTrue(ServiceQuery.getInstance().removeFromWishlist(userId, documentId));
    }

    @Test
    public void testGetUndoneServicesTest() {
        int userId = 5;
        int documentId = 1;
        System.out.println(ServiceQuery.getInstance().getUndoneService(userId, documentId));
    }

    @Test
    public void testBorrowDocument() {
        int userId = 5;
        int documentId = 1;
        Assertions.assertTrue(ServiceQuery.getInstance().borrowDocument(userId, DocumentQuery.getInstance().getById(documentId)));
    }

    @Test void testExecuteBorrowRequest() {
        int userId = 5;
        int documentId = 1;
        Assertions.assertTrue(ServiceQuery.getInstance().executeBorrowRequest(userId, documentId, false));
    }

    @Test
    public void testReturnDocument() {
        int userId = 5;
        int documentId = 1;
        Assertions.assertTrue(ServiceQuery.getInstance().returnDocument(userId, DocumentQuery.getInstance().getById(documentId)));
    }

    @Test
    public void testGetOverdueDocuments() {
        int userId = 4;
        List<Document> overdueDocuments = ServiceQuery.getInstance().getOverdueDocuments(userId);
        for (Document overdueDocument : overdueDocuments) {
            System.out.println(overdueDocument);
        }
        Assertions.assertNotNull(overdueDocuments);
    }

    @Test
    public void testGetServiceData() {
        int userId = 2;
        List<ServiceData> data = ServiceQuery.getInstance().getServiceData(3);
        for (ServiceData serviceData : data) {
            System.out.println(serviceData);
        }
        Assertions.assertNotNull(data);
    }
}
