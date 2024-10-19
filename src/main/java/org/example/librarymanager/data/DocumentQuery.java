package org.example.librarymanager.data;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;
import org.example.librarymanager.models.Comment;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.Rating;

import javax.print.Doc;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.example.librarymanager.Config.API_KEY;
import static org.example.librarymanager.Config.APPLICATION_NAME;

public class DocumentQuery {
//    private static final String APPLICATION_NAME = "LibraryManager";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public static Document getDocumentById(int id) {
        Document document = null;
        try (Connection connection = DatabaseConnection.getConnection();) {
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

    public static List<Document> getDocuments(String order, int limit) {
        List<Document> documents = new ArrayList<Document>();
        try (Connection connection = DatabaseConnection.getConnection();) {

            String query = "select * from documents";
            if (order != null && !order.isEmpty()) {
                query += " order by " + order;
            }
            if (limit > 0) {
                query += " limit " + limit;
            }
            PreparedStatement ps = connection.prepareStatement(query);
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
    public static List<Document> getDocumentsByCategory(String order, int limit) {
        List<Document> documents = new ArrayList<Document>();
        try (Connection connection = DatabaseConnection.getConnection();) {

            String query = "select d.* from documents d join categories c on d.categoryId = c.id";
            if (order != null && !order.isEmpty()) {
                query += " where c.name = '" + order + "'";
            }
            if (limit > 0) {
                query += " limit " + limit;
            }
            PreparedStatement ps = connection.prepareStatement(query);
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
    public static List<Document> getMostPopularDocuments(int limit) {
        return getDocuments("borrowedTimes desc", limit);
    }

    public static List<Document> getHighestRatedDocuments(int limit) {
        List<Document> documents = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();) {
            String query = "select documents.*, avg(ratings.value) rating\n"
                    + "from documents\n"
                    + "left join ratings on documents.id = ratings.documentId\n"
                    + "group by documents.id\n"
                    + "order by rating desc";
            if (limit > 0) query += " limit " + limit;
            PreparedStatement ps = connection.prepareStatement(query);
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

    public static List<Document> getDocumentsByOwner(int owner) {
        List<Document> documents = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("select * from documents where owner = ?");
            ps.setInt(1, owner);
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

    public static Document addDocument(int categoryId, int owner, String author, String title, String description, String imageLink, int quantity) {
        Document document = null;
        try (Connection connection = DatabaseConnection.getConnection();){
            PreparedStatement ps = connection.prepareStatement("insert into documents (categoryId, owner, author, title, description, imageLink, quantity) values(?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, categoryId);
            ps.setInt(2, owner);
            ps.setString(3, author);
            ps.setString(4, title);
            ps.setString(5, description);
            ps.setString(6, imageLink);
            ps.setInt(7, quantity);
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
        try (Connection connection = DatabaseConnection.getConnection();) {
            PreparedStatement ps = connection.prepareStatement("update documents set " +
                    "categoryId = ?, author = ?, title = ?, description = ?, imageLink = ?, " +
                    "quantity = ?, quantityInStock = ?, borrowedTimes = ? " +
                    "where id = ?"
            );
            ps.setInt(1, document.getCategoryId());
            ps.setString(2, document.getAuthor());
            ps.setString(3, document.getTitle());
            ps.setString(4, document.getDescription());
            ps.setString(5, document.getImageLink());
            ps.setInt(6, document.getQuantity());
            ps.setInt(7, document.getQuantityInStock());
            ps.setInt(8, document.getBorrowedTimes());
            ps.setInt(9, document.getId());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteDocument(Document document) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("delete from documents where id = ?");
            ps.setInt(1, document.getId());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean comment(int userId, int documentId, String content) {
        try (Connection connection = DatabaseConnection.getConnection();) {
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
        try (Connection connection = DatabaseConnection.getConnection();) {
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

    /**
     * Get list of books from Google Books API by a search pattern
     * @param pattern
     * @return
     */
    public static List<Volume> getDocumentsFromAPI(String pattern) {
        try {
            Books books = new Books.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, null)
                    .setApplicationName(APPLICATION_NAME).build();
            Books.Volumes.List volumeList = books.volumes().list(pattern).setKey(API_KEY);
            volumeList.setMaxResults(30L);
            Volumes volumes = volumeList.execute();
            return volumes.getItems();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get book's information from Google Books API by ISBN
     * @param ISBN
     * @return Voulume
     */
    public static Volume getDocumentByISBN(String ISBN) {
        try {
            Books books = new Books.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, null)
                    .setApplicationName(APPLICATION_NAME).build();
            Books.Volumes.List volumeList = books.volumes().list("isbn:" + ISBN).setKey(API_KEY);
            Volumes volumes = volumeList.execute();
            if (volumes.getItems() != null && !volumes.getItems().isEmpty()) {
                return volumes.getItems().getFirst();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Rating getRating(int userId, int documentId) {
        Rating rating = null;
        try (Connection connection = DatabaseConnection.getConnection();) {
            PreparedStatement ps = connection.prepareStatement("select * from ratings where userId = ? and documentId = ?");
            ps.setInt(1, userId);
            ps.setInt(2, documentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rating = new Rating(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rating;
    }

    public static void rateDocument(int userId, int documentId, float value) {
        try (Connection connection = DatabaseConnection.getConnection();) {
            Rating rating = getRating(userId, documentId);
            if (rating == null) {
                PreparedStatement ps = connection.prepareStatement("insert into ratings (userId, documentId, value) values(?,?,?)");
                ps.setInt(1, userId);
                ps.setInt(2, documentId);
                ps.setFloat(3, value);
                ps.executeUpdate();
                ps.close();
            }
            else {
                PreparedStatement ps = connection.prepareStatement("update ratings set value = ? where userId = ? and documentId = ?");
                ps.setFloat(1, value);
                ps.setInt(2, userId);
                ps.setInt(3, documentId);
                ps.executeUpdate();
                ps.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
