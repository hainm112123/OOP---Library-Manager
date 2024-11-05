package org.example.librarymanager.data;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;
import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.Rating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.example.librarymanager.Config.API_KEY;
import static org.example.librarymanager.Config.APPLICATION_NAME;

public class DocumentQuery implements DataAccessObject<Document> {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static DocumentQuery instance;
    private DatabaseConnection databaseConnection;

    private DocumentQuery(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public static synchronized DocumentQuery getInstance() {
        if (instance == null) {
            instance = new DocumentQuery(DatabaseConnection.getInstance());
        }
        return instance;
    }

    /**
     * Get a document by id
     */
    @Override
    public Document getById(int id) {
        Document document = null;
        try (Connection connection = databaseConnection.getConnection()) {
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

    @Override
    public List<Document> getAll() {
        List<Document> documents = new ArrayList<Document>();
        try (Connection connection = databaseConnection.getConnection();) {

            String query = "select * from documents";
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

    /**
     * Get documents by order and limit.
     */
    public List<Document> getDocuments(String order, int limit) {
        List<Document> documents = new ArrayList<Document>();
        try (Connection connection = databaseConnection.getConnection();) {

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

    /**
     * Get documents by category.
     */
    public List<Document> getDocumentsByCategory(String order, int limit) {
        List<Document> documents = new ArrayList<Document>();
        try (Connection connection = databaseConnection.getConnection();) {

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

    /**
     * Get documents by borrowed time, descending.
     */
    public List<Document> getMostPopularDocuments(int limit) {
        return getDocuments("borrowedTimes desc", limit);
    }

    /**
     * Get documents by ratings, descending.
     */
    public List<Document> getHighestRatedDocuments(int limit) {
        List<Document> documents = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();) {
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

    /**
     * Get documents by owner.
     */
    public List<Document> getDocumentsByOwner(int owner) {
        List<Document> documents = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection()) {
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

    /**
     * Add a new document to database.
     */
    @Override
    public Document add(Document document) {
        Document documentEntity = null;
        try (Connection connection = databaseConnection.getConnection();){
            PreparedStatement ps = connection.prepareStatement("insert into documents (categoryId, owner, author, title, description, imageLink, quantity) values(?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, document.getCategoryId());
            ps.setInt(2, document.getOwner());
            ps.setString(3, document.getAuthor());
            ps.setString(4, document.getTitle());
            ps.setString(5, document.getDescription());
            ps.setString(6, document.getImageLink());
            ps.setInt(7, document.getQuantity());
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                documentEntity = getById(generatedKeys.getInt(1));
            }
            generatedKeys.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentEntity;
    }

    /**
     * Update an existed document in database.
     */
    @Override
    public boolean update(Document document) {
        try (Connection connection = databaseConnection.getConnection();) {
            Trie.getInstance().addTrie(document.getTitle(), document.getId());
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

    /**
     * Delete an existed document.
     */
    @Override
    public boolean delete(Document document) {
        try (Connection connection = databaseConnection.getConnection()) {
            Trie.getInstance().delTrie(document.getTitle());
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

    /**
     * Get list of books from Google Books API by a search pattern.
     */
    public List<Volume> getDocumentsFromAPI(String pattern) {
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
     * Get book's information from Google Books API by ISBN.
     */
    public Volume getDocumentByISBN(String ISBN) {
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
  
    /**
     * Add a rating to database.
     */
    public boolean rateDocument(int userId, int documentId, double value, String content) {
        try (Connection connection = databaseConnection.getConnection();) {
            PreparedStatement ps = connection.prepareStatement("insert into ratings (userId, documentId, value, content) values(?,?,?,?)");
            ps.setInt(1, userId);
            ps.setInt(2, documentId);
            ps.setDouble(3, value);
            ps.setString(4, content);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get ratings by document id in database.
     */
    public List<Rating> getDocumentRatings(int documentId) {
        List<Rating> ratings = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("select * from ratings where documentId = ? order by postedTime desc");
            ps.setInt(1, documentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ratings.add(new Rating(rs));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ratings;
    }
}