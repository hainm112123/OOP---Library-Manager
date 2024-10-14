package org.example.librarymanager.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.example.librarymanager.data.AuthQuery;
import org.example.librarymanager.data.AuthResult;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController extends ControllerWrapper {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private TextField firstname;
    @FXML
    private TextField lastname;
    @FXML
    private TextField gender;
    @FXML
    private DatePicker dateOfBirth;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Label registerMessageLabel;
    @FXML
    private Hyperlink loginHyperlink;

    public void registerButtonOnAction(ActionEvent event) {
        AuthResult registerResult = AuthQuery.register(username.getText(), password.getText(), confirmPassword.getText(), firstname.getText(), lastname.getText(), gender.getText(), dateOfBirth.getValue());
        registerMessageLabel.setText(registerResult.getMessage());
    }

    public void loginButtonOnAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            switchScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
