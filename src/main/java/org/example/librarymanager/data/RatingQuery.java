package org.example.librarymanager.data;

import org.example.librarymanager.models.Rating;

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
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ratings");
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
    public boolean delete(Rating entity) {
        return false;
    }
}
