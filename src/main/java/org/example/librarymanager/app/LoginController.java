package org.example.librarymanager.app;

import org.example.librarymanager.data.DatabaseConnection;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class LoginController extends ControllerWrapper {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ProfileController.setStage(stage);
        Connection connection = DatabaseConnection.getConnection();
    }
}
