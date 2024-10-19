package org.example.librarymanager.data;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static org.example.librarymanager.Config.*;

public class DatabaseConnection {
    private static BasicDataSource ds;

    static {
        ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl(DB_URL);
        ds.setUsername(DB_USER);
        ds.setPassword(DB_PASSWORD);
        ds.setMinIdle(5);
        ds.setInitialSize(5);
        ds.setMaxIdle(20);
        ds.setMaxTotal(50);
        ds.setMaxOpenPreparedStatements(100);
    }

    public static Connection getConnection() throws SQLException {
        logPoolStatus();
        return ds.getConnection();
    }

    public synchronized static void logPoolStatus() throws SQLException {
        System.out.println("+ Num of Active Connections: " + ds.getNumActive());
        System.out.println("+ Num of Idle Connections: " + ds.getNumIdle());
    }
}
