package org.example.librarymanager.data;

import org.example.librarymanager.models.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryQuery implements DataAccessObject<Category> {
    private static CategoryQuery instance;
    private DatabaseConnection databaseConnection;

    private CategoryQuery(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public static synchronized CategoryQuery getInstance() {
        if (instance == null) {
            instance = new CategoryQuery(DatabaseConnection.getInstance());
        }
        return instance;
    }

    /**
     * Get all in table categories.
     */
    @Override
    public List<Category> getAll() {
        List<Category> categories = new ArrayList<Category>();
        try (Connection connection = databaseConnection.getConnection();) {
            PreparedStatement ps = connection.prepareStatement("select * from categories");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categories.add(new Category(rs));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }


    /**
     * Get all name in table categories.
     */
    public List<String> getCategoriesName() {
        List<String> categories = new ArrayList<String>();
        try (Connection connection = databaseConnection.getConnection();) {
            PreparedStatement ps = connection.prepareStatement("select name from categories");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }

    /**
     * Get category by id in table categories.
     */
    @Override
    public Category getById(int id) {
        Category category = null;
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("select * from categories where id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                category = new Category(rs);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return category;
    }

    @Override
    public Category add(Category category) {
        return null;
    }

    @Override
    public boolean update(Category category) {
        return false;
    }

    @Override
    public boolean delete(Category category) {
        return false;
    }
}
