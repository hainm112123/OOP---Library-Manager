package org.example.librarymanager.data;

import org.example.librarymanager.models.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryQuery {
    public static List<Category> getCategories() {
        List<Category> categories = new ArrayList<Category>();
        try (Connection connection = DatabaseConnection.getConnection();) {
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

    public static List<String> getCategoriesName() {
        List<String> categories = new ArrayList<String>();
        try (Connection connection = DatabaseConnection.getConnection();) {
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


    public static Category getCategory(int id) {
        Category category = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
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
}
