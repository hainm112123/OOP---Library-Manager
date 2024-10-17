package org.example.librarymanager.data;

import com.google.api.services.books.model.Volume;
import org.example.librarymanager.models.Comment;
import org.example.librarymanager.models.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DocumentTest {
    @Test
    public void addDocumentTest() {
        int categoryId = 1;
        int owner = 1;
        String author = "abc";
        String title = "title";
        String description = "description";
        String content = "content";
        int quantity = 5;
        Document document = DocumentQuery.addDocument(categoryId, owner, author, title, description, content, quantity);
        Assertions.assertEquals(categoryId, document.getCategoryId());
        Assertions.assertEquals(owner, document.getOwner());
        Assertions.assertEquals(title, document.getTitle());
    }

    @Test
    public void getDocumentsTest() {
        List<Document> documents = DocumentQuery.getHighestRatedDocuments(10);
        for (Document document : documents) {
            System.out.println(document);
        }
//        Assertions.assertEquals(documents.size(), 1);
    }

    @Test
    public void commentTest() {
        int userId = 1;
        int documentId = 2;
        String content = "comment";
        Assertions.assertTrue(DocumentQuery.comment(userId, documentId, content));
    }

    @Test
    public void getCommentsTest() {
        int documentId = 2;
        List<Comment> comments = DocumentQuery.getComments(documentId);
        Assertions.assertEquals(comments.size(), 1);
    }

    @Test
    public void getDocumentsFromAPI_Test() {
        String pattern = "Harry Potter";
        List<Volume> volumes = DocumentQuery.getDocumentsFromAPI(pattern);
        Assertions.assertNotNull(volumes);
        for (Volume volume : volumes) {
            System.out.println("Title: " + volume.getVolumeInfo().getTitle());
            System.out.println("Authors: " + volume.getVolumeInfo().getAuthors());
        }
    }

    @Test
    public void getDocumentByISBN_Test() {
        Volume volume = DocumentQuery.getDocumentByISBN("9781975309596");
        Assertions.assertNotNull(volume);
        System.out.println("Title: " + volume.getVolumeInfo().getTitle());
        System.out.println("Authors: " + volume.getVolumeInfo().getAuthors());
        System.out.println("Categories: " + volume.getVolumeInfo().getCategories());
        System.out.println(volume.getVolumeInfo().getImageLinks());
    }

    @Test
    public void rateDocumentTest() {
        int userId = 2;
        int documentId = 3;
        float value = (float)3;
        DocumentQuery.rateDocument(userId, documentId, value);
    }
}
