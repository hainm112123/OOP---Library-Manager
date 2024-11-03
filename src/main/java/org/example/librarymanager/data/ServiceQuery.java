package org.example.librarymanager.data;

import org.example.librarymanager.models.Document;
import org.example.librarymanager.models.Service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class ServiceQuery {
    public static Service getUndoneService(int userId, int documentId) {
        Service service = null;
        try (Connection connection = DatabaseConnection.getInstance().getConnection();) {
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

    public static boolean isBorrowingDocument(int userId, int documentId) {
        return getUndoneService(userId, documentId) != null;
    }

    public static void updateService(Service service) {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();) {
            PreparedStatement ps = connection.prepareStatement("update services set returnDate = ? where id = ?");
            ps.setDate(1, Date.valueOf(service.getReturnDate()));
            ps.setInt(2, service.getId());
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean borrowDocument(int userId, int documentId) {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();) {
            Service service = getUndoneService(userId, documentId);
            if (service != null) return false;
            Document document = DocumentQuery.getDocumentById(documentId);
            if (document == null || document.getQuantityInStock() < 1) return false;

            document.setQuantityInStock(document.getQuantityInStock() - 1);
            document.setBorrowedTimes(document.getBorrowedTimes() + 1);
            DocumentQuery.updateDocument(document);

            PreparedStatement ps = connection.prepareStatement("insert into services (userId, documentId) values(?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, documentId);
            ps.executeUpdate();
            ps.close();

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean returnDocument(int userId, int documentId) {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();) {
            Service service = getUndoneService(userId, documentId);
            if (service == null) return false;
            Document document = DocumentQuery.getDocumentById(documentId);
            if (document == null) return false;

            document.setQuantityInStock(document.getQuantityInStock() + 1);
            DocumentQuery.updateDocument(document);

            service.setReturnDate(LocalDate.now());
            updateService(service);

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
