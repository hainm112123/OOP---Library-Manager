package org.example.librarymanager.data;

import org.example.librarymanager.models.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DocumentQueryTest {
    @Test
    public void addDocumentTest() {
        int categoryId = 1;
        int author = 3;
        String title = "title3";
        String description = "description3";
        String content = "content3";
        int quantity = 5;
        Document document = DocumentQuery.addDocument(categoryId, author, title, description, content, quantity);
        Assertions.assertEquals(categoryId, document.getCategoryId());
        Assertions.assertEquals(author, document.getAuthor());
        Assertions.assertEquals(title, document.getTitle());
    }
}
