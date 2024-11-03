package org.example.librarymanager.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.librarymanager.data.AuthQuery;
import org.example.librarymanager.data.AuthResult;

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
        AuthResult registerResult = AuthQuery.getInstance().register(username.getText(), password.getText(), confirmPassword.getText(), firstname.getText(), lastname.getText(), gender.getText(), dateOfBirth.getValue());
        registerMessageLabel.setText(registerResult.getMessage());
    }

    public void loginButtonOnAction(ActionEvent event) {
        safeSwitchScene("login.fxml");
        stage.show();
    }
}
