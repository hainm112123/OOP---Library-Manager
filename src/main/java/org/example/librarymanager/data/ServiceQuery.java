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
            PreparedStatement ps = connection.prepareStatement("select * from services where userId= ? and documentId= ? and status != ?");
            ps.setInt(1, userId);
            ps.setInt(2, documentId);
            ps.setInt(3, Service.STATUS_COMPLETED);
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

    /**
     * check if user is currently borrow a specific document
     * @param userId
     * @param documentId
     * @return
     */
    public boolean isBorrowingDocument(int userId, int documentId) {
        Service service = getUndoneService(userId, documentId);
        return service != null && service.getStatus() == Service.STATUS_READING;
    }

    public int getStatus(int userId, int documentId) {
        Service service = getUndoneService(userId, documentId);
        return service == null ? Service.STATUS_COMPLETED : service.getStatus();
    }

    @Override
    public List<Service> getAll() {
        List<Service> services = new ArrayList<Service>();
        try (Connection connection = databaseConnection.getConnection();) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM services");
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
    public Service add(Service service) {
        try (Connection connection = databaseConnection.getConnection();) {
            PreparedStatement ps = connection.prepareStatement("insert into services (userId, documentId, status, borrowDate, returnDate) values(?, ?, ?, ?, ?)");
            ps.setInt(1, service.getUserId());
            ps.setInt(2, service.getDocumentId());
            ps.setInt(3, service.getStatus());
            ps.setDate(4, service.getBorrowDate() != null ? Date.valueOf(service.getBorrowDate()) : null);
            ps.setDate(5, service.getReturnDate() != null ? Date.valueOf(service.getReturnDate()) : null);
            ps.executeUpdate();
            ps.close();
            return service;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Service service) {
        try (Connection connection = databaseConnection.getConnection();) {
            PreparedStatement ps = connection.prepareStatement("update services set status = ?, borrowDate = ?, returnDate = ? where id = ?");
            ps.setInt(1, service.getStatus());
            ps.setDate(2, service.getBorrowDate() != null ? Date.valueOf(service.getBorrowDate()) : null);
            ps.setDate(3, service.getReturnDate() != null ? Date.valueOf(service.getReturnDate()) : null);
            ps.setInt(4, service.getId());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Service service) {
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("delete from services where id = ?");
            ps.setInt(1, service.getId());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * add a document to user's wishlist
     * @param userId
     * @param documentId
     * @return
     */
    public boolean addToWishlist(int userId, int documentId) {
        Service service = getUndoneService(userId, documentId);
        if (service != null) return false;
        service = new Service();
        service.setUserId(userId);
        service.setDocumentId(documentId);
        service.setStatus(Service.STATUS_WISH_LIST);
        add(service);
        return true;
    }

    /**
     * remove a document from user's wishlist
     * @param userId
     * @param documentId
     * @return
     */
    public boolean removeFromWishlist(int userId, int documentId) {
        Service service = getUndoneService(userId, documentId);
        if (service == null || service.getStatus() != Service.STATUS_WISH_LIST) return false;
        return delete(service);
    }

    /**
     * handle borrow document
     * @param userId
     * @param document
     * @return
     */
    public boolean borrowDocument(int userId, Document document) {
        Service service = getUndoneService(userId, document.getId());
        if (service != null && service.getStatus() != Service.STATUS_WISH_LIST) {
            return false;
        }
        if (document.getQuantityInStock() < 1) return false;

        boolean result = false;
        if (service != null) {
            service.setStatus(Service.STATUS_READING);
            service.setBorrowDate(LocalDate.now());
            result = update(service);
        } else {
            service = new Service();
            service.setUserId(userId);
            service.setDocumentId(document.getId());
            service.setStatus(Service.STATUS_READING);
            service.setBorrowDate(LocalDate.now());
            result = add(service) != null;
        }

        if (result) {
            document.setQuantityInStock(document.getQuantityInStock() - 1);
            document.setBorrowedTimes(document.getBorrowedTimes() + 1);
            DocumentQuery.getInstance().update(document);
        }
        return result;
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
            if (service.getStatus() != Service.STATUS_READING) return false;

            document.setQuantityInStock(document.getQuantityInStock() + 1);
            DocumentQuery.getInstance().update(document);

            service.setReturnDate(LocalDate.now());
            service.setStatus(Service.STATUS_COMPLETED);
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
                    "where s.userId = ? and status = ? and s.returnDate is null and datediff(CURRENT_DATE, s.borrowDate) > ?"
            );
            ps.setInt(1, userId);
            ps.setInt(2, Service.STATUS_READING);
            ps.setInt(3, EXPIRATION_DAYS);
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
            PreparedStatement ps = connection.prepareStatement("select userId, count(*) as count, borrowDate as date from services where userId = ? and status != ? group by date");
            ps.setInt(1, userId);
            ps.setInt(2, Service.STATUS_WISH_LIST);
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

    public List<Document> getWishlistDocuments(int userId) {
        List<Document> documents = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(
                    "select d.* from services as s join documents as d on s.documentId = d.id " +
                            "where s.userId = ? and status = ? group by d.id"
            );
            ps.setInt(1, userId);
            ps.setInt(2, Service.STATUS_WISH_LIST);
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

    public List<Document> getBorrowingDocuments(int userId) {
        List<Document> documents = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(
                    "select d.* from services as s join documents as d on s.documentId = d.id " +
                            "where s.userId = ? and status = ? group by d.id"
            );
            ps.setInt(1, userId);
            ps.setInt(2, Service.STATUS_READING);
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

    public List<Document> getCompletedDocument(int userId) {
        List<Document> documents = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(
                    "select d.* from services as s join documents as d on s.documentId = d.id " +
                            "where s.userId = ? and status = ? group by d.id"
            );
            ps.setInt(1, userId);
            ps.setInt(2, Service.STATUS_COMPLETED);
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
}
