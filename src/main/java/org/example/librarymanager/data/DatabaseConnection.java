package org.example.librarymanager.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.example.librarymanager.Config.*;

public class DatabaseConnection {
    private static Connection connection;

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
