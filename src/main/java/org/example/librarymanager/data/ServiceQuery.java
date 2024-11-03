package org.example.librarymanager.data;

import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.Service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

public class ServiceQuery implements DataAccessObject<Service> {
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

    public boolean isBorrowingDocument(int userId, int documentId) {
        return getUndoneService(userId, documentId) != null;
    }

    @Override
    public List<Service> getAll() {
        return List.of();
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
}
