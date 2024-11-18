package org.example.librarymanager.data;

import org.example.librarymanager.models.Rating;
import org.example.librarymanager.models.RecommendationData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RatingQuery implements DataAccessObject<Rating> {
    private static RatingQuery instance;
    private DatabaseConnection databaseConnection;

    public RatingQuery(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public static RatingQuery getInstance() {
        if (instance == null) {
            instance = new RatingQuery(DatabaseConnection.getInstance());
        }
        return instance;
    }

    @Override
    public List<Rating> getAll() {
        List<Rating> ratings = new ArrayList<Rating>();
        try (Connection connection = databaseConnection.getConnection();) {
            String query = "select ratings.*, users.username userName, documents.title documentName"
                    + " from ratings"
                    + " left join users on ratings.userId = users.id"
                    + " left join documents on ratings.documentId = documents.id\n"
                    + "group by ratings.id\n";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ratings.add(new Rating(resultSet));
            }
            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ratings;
    }

    @Override
    public Rating getById(int id) {
        return null;
    }

    @Override
    public Rating add(Rating entity) {
        return null;
    }

    @Override
    public boolean update(Rating entity) {
        return false;
    }

    @Override
    public boolean delete(Rating rating) {
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("delete from ratings where id = ?");
            ps.setInt(1, rating.getId());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<RecommendationData> getDataModel() {
        List<RecommendationData> dataModel = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("select ratings.userId, ratings.documentId, avg(ratings.value) as preference from ratings group by ratings.userId, ratings.documentId");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                dataModel.add(new RecommendationData(rs));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataModel;
    }
}
