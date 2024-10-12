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
        int author = 1;
        String title = "title";
        String description = "description";
        String content = "content";
        int quantity = 5;
        Document document = DocumentQuery.addDocument(categoryId, author, title, description, content, quantity);
        Assertions.assertEquals(categoryId, document.getCategoryId());
        Assertions.assertEquals(author, document.getAuthor());
        Assertions.assertEquals(title, document.getTitle());
    }

    @Test
    public void getDocumentsTest() {
        List<Document> documents = DocumentQuery.getDocuments();
        Assertions.assertEquals(documents.size(), 1);
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
        Volume volume = DocumentQuery.getDocumentByISBN("197530957X");
        Assertions.assertNotNull(volume);
        System.out.println("Title: " + volume.getVolumeInfo().getTitle());
        System.out.println("Authors: " + volume.getVolumeInfo().getAuthors());
        System.out.println(volume.getAccessInfo().getWebReaderLink());
    }
}
