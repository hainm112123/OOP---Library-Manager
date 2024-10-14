package org.example.librarymanager.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.librarymanager.data.AuthQuery;
import org.example.librarymanager.data.AuthResult;
import org.example.librarymanager.data.DatabaseConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class LoginController extends ControllerWrapper {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //LoginController.setStage(stage);
    }


    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Hyperlink registerHyperlink;
    @FXML
    private Label loginMessageLabel;

    public void loginButtonOnAction(ActionEvent event) {
        AuthResult loginResult = AuthQuery.login(username.getText(), password.getText());
        loginMessageLabel.setText(loginResult.getMessage());
        if(loginResult.getUser() != null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource("document-detail.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                switchScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerButtonOnAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource("document-detail.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            switchScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
