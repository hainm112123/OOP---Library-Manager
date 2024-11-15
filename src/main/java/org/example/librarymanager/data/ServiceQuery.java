package org.example.librarymanager.data;

import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.Rating;
import org.example.librarymanager.models.Service;
import org.example.librarymanager.models.ServiceData;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceQuery implements DataAccessObject<Service> {
    private static final int EXPIRATION_DAYS = 1;
    private static ServiceQuery instance;
    private DatabaseConnection databaseConnection;

    private ServiceQuery(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public static synchronized ServiceQuery getInstance() {
        if (instance == null) {
            instance = new ServiceQuery(DatabaseConnection.getInstance());
        }
        return instance;
    }

    /**
     * get service of a specific user and the document if user haven't returned it
     * @param userId
     * @param documentId
     * @return
     */
    private Service getUndoneService(int userId, int documentId) {
        Service service = null;
        try (Connection connection = databaseConnection.getConnection();) {
            PreparedStatement ps = connection.prepareStatement("select * from services where userId= ? and documentId= ? and returnDate is null");
            ps.setInt(1, userId);
            ps.setInt(2, documentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                service = new Service(rs);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return service;
    }

    public List<Document> getBorrowingDocuments(int userId) {
        List<Document> documents = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(
                    "select d.* from services as s join documents as d on s.documentId = d.id " +
                            "where s.userId = ? and s.returnDate is null;"
            );
            ps.setInt(1, userId);
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

    public boolean isBorrowingDocument(int userId, int documentId) {
        return getUndoneService(userId, documentId) != null;
    }

    @Override
    public List<Service> getAll() {
        List<Service> services = new ArrayList<Service>();
        try (Connection connection = databaseConnection.getConnection();) {
            String query = "select services.*, users.username borrowerName, documents.title documentName"
                    + " from services"
                    + " left join users on services.userId = users.id"
                    + " left join documents on services.documentId = documents.id\n"
                    + "group by services.id\n";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                services.add(new Service(resultSet));
            }
            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return services;
    }

    @Override
    public Service getById(int id) {
        return null;
    }

    @Override
    public Service add(Service entity) {
        return null;
    }

    @Override
    public boolean update(Service service) {
        try (Connection connection = databaseConnection.getConnection();) {
            PreparedStatement ps = connection.prepareStatement("update services set returnDate = ? where id = ?");
            ps.setDate(1, Date.valueOf(service.getReturnDate()));
            ps.setInt(2, service.getId());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Service entity) {
        return false;
    }

    /**
     * handle borrow document
     * @param userId
     * @param document
     * @return
     */
    public boolean borrowDocument(int userId, Document document) {
        try (Connection connection = databaseConnection.getConnection();) {
            Service service = getUndoneService(userId, document.getId());
            if (service != null) return false;
            if (document.getQuantityInStock() < 1) return false;

            document.setQuantityInStock(document.getQuantityInStock() - 1);
            document.setBorrowedTimes(document.getBorrowedTimes() + 1);
            DocumentQuery.getInstance().update(document);

            PreparedStatement ps = connection.prepareStatement("insert into services (userId, documentId) values(?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, document.getId());
            ps.executeUpdate();
            ps.close();

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * handle return document
     * @param userId
     * @param document
     * @return
     */
    public boolean returnDocument(int userId, Document document) {
        try {
            Service service = getUndoneService(userId, document.getId());
            if (service == null) return false;

            document.setQuantityInStock(document.getQuantityInStock() + 1);
            DocumentQuery.getInstance().update(document);

            service.setReturnDate(LocalDate.now());
            update(service);

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * get overdue document of user
     * @param userId
     * @return
     */
    public List<Document> getOverdueDocuments(int userId) {
        List<Document> documents = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("select d.* from documents as d " +
                    "join services as s on s.documentId = d.id " +
                    "where s.userId = ? and s.returnDate is null and datediff(CURRENT_DATE, s.borrowDate) > ?"
            );
            ps.setInt(1, userId);
            ps.setInt(2, EXPIRATION_DAYS);
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

    public List<ServiceData> getServiceData(int userId) {
        List<ServiceData> data = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("select userId, count(*) as count, borrowDate as date from services where userId = ? group by date");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.add(new ServiceData(rs));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
