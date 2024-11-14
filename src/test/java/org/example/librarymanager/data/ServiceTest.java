package org.example.librarymanager.data;

import org.example.librarymanager.models.ServiceData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    public void testGetOverdueDocuments() {
        int userId = 4;
        Assertions.assertNotNull(ServiceQuery.getInstance().getOverdueDocuments(userId));
    }

    @Test
    public void testGetServiceData() {
        int userId = 2;
        List<ServiceData> data = ServiceQuery.getInstance().getServiceData(userId);
        for (ServiceData serviceData : data) {
            System.out.println(serviceData);
        }
        Assertions.assertNotNull(data);
    }
}
