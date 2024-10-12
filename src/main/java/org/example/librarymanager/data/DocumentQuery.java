package org.example.librarymanager.data;

import org.example.librarymanager.models.Comment;
import org.example.librarymanager.models.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DocumentQuery {
    public static Document getDocumentById(int id) {
        Document document = null;
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("select * from documents where id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                document = new Document(rs);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    public static List<Document> getDocuments() {
        List<Document> documents = new ArrayList<Document>();
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("select * from documents");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                documents.add(new Document(rs));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documents;
    }

    public static Document addDocument(int categoryId, int author, String title, String description, String content, int quantity) {
        Document document = null;
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("insert into documents (categoryId, author, title, description, content, quantity) values(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, categoryId);
            ps.setInt(2, author);
            ps.setString(3, title);
            ps.setString(4, description);
            ps.setString(5, content);
            ps.setInt(6, quantity);
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                document = getDocumentById(generatedKeys.getInt(1));
            }
            generatedKeys.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    public static boolean updateDocument(Document document) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("update documents set quantityInStock = ?, borrowedTimes = ? where id = ?");
            ps.setInt(1, document.getQuantityInStock());
            ps.setInt(2, document.getBorrowedTimes());
            ps.setInt(3, document.getId());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean comment(int userId, int documentId, String content) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("insert into comments (userId, documentId, content) values(?,?,?)");
            ps.setInt(1, userId);
            ps.setInt(2, documentId);
            ps.setString(3, content);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Comment> getComments(int documentId) {
        List<Comment> comments = new ArrayList<>();
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("select * from comments where documentId = ?");
            ps.setInt(1, documentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                comments.add(new Comment(rs));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comments;
    }
}
