package org.example.librarymanager.data;

import com.google.api.services.books.model.Volume;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.example.librarymanager.models.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
        Document document = DocumentQuery.getInstance().add(new Document(categoryId, owner, author, title, description, content, quantity));
        Assertions.assertEquals(categoryId, document.getCategoryId());
        Assertions.assertEquals(owner, document.getOwner());
        Assertions.assertEquals(title, document.getTitle());
    }

    @Test
    public void getDocumentsTest() {
        List<Document> documents = DocumentQuery.getInstance().getDocuments("",10);
        for (Document document : documents) {
            System.out.println(document);
        }
//        Assertions.assertEquals(documents.size(), 1);
    }

    @Test
    public void getDocumentsFromAPI_Test() {
        String pattern = "Harry Potter";
        List<Volume> volumes = DocumentQuery.getInstance().getDocumentsFromAPI(pattern);
        Assertions.assertNotNull(volumes);
        for (Volume volume : volumes) {
            System.out.println("Title: " + volume.getVolumeInfo().getTitle());
            System.out.println("Authors: " + volume.getVolumeInfo().getAuthors());
        }
    }

    @Test
    public void getDocumentByISBN_Test() {
        Volume volume = DocumentQuery.getInstance().getDocumentByISBN("0316559806");
        Assertions.assertNotNull(volume);
        System.out.println("Title: " + volume.getVolumeInfo().getTitle());
        System.out.println("Authors: " + volume.getVolumeInfo().getAuthors());
        System.out.println("Categories: " + volume.getVolumeInfo().getCategories());
        System.out.println(volume.getVolumeInfo().getImageLinks());
    }

    @Test void getDocumentsByOwnerTest() {
        List<Document> documents = DocumentQuery.getInstance().getDocumentsByOwner(2);
        for (Document document : documents) {
            System.out.println(document);
        }
        Assertions.assertNotNull(documents);
    }

    @Test
    public void rateDocumentTest() {
        int userId = 1;
        int documentId = 1;
        float value = (float)5;
        DocumentQuery.getInstance().rateDocument(userId, documentId, value, "Lorem ipsum dolor sit amet, " +
                "consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore " +
                "magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco " +
                "laboris nisi ut aliquip ex ea commodo consequat"
        );
    }

    @Test
    public void getDocumentsByCategoryTest() {
        List<Document> documents = DocumentQuery.getInstance().getDocumentsByCategory(2);
        for (Document document : documents) {
            System.out.println(document);
        }
        Assertions.assertNotNull(documents);
    }

    public void addLucenceDocument(IndexWriter writer, String title) throws IOException {
        org.apache.lucene.document.Document luceneDocument = new org.apache.lucene.document.Document();
        luceneDocument.add(new TextField("title", title, Field.Store.YES));
        writer.addDocument(luceneDocument);
    }

    @Test
    public void lucenceDocumentTest() throws Exception {
        Directory memoryIndex = new RAMDirectory();
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(memoryIndex, config);

        addLucenceDocument(writer, "laid-back camp");
        addLucenceDocument(writer, "attack on titan");
        addLucenceDocument(writer, "sound! euphonium");

        writer.close();

        Query query = new QueryParser("title", analyzer).parse("euphonium");
        IndexReader indexReader = DirectoryReader.open(memoryIndex);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 1);
        for (ScoreDoc doc: topDocs.scoreDocs) {
            System.out.println(searcher.doc(doc.doc));
        }
    }
}
