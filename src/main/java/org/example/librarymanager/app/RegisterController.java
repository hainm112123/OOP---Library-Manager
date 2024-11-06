package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.librarymanager.Common;
import org.example.librarymanager.data.AuthQuery;
import org.example.librarymanager.data.AuthResult;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController extends ControllerWrapper {
    @FXML
    private MFXTextField firstname;
    @FXML
    private MFXTextField lastname;
    @FXML
    private MFXComboBox<String> gender;
    @FXML
    private MFXDatePicker dateOfBirth;
    @FXML
    private MFXTextField username;
    @FXML
    private MFXPasswordField password;
    @FXML
    private MFXPasswordField confirmPassword;
    @FXML
    private Label registerMessageLabel;
    @FXML
    private Hyperlink loginHyperlink;
    @FXML
    private MFXProgressSpinner loader;
    @FXML
    private MFXButton submitBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gender.getItems().addAll("Male", "Female");
        Common.disable(loader);
    }

    public void registerButtonOnAction(ActionEvent event) {
        Task<AuthResult> task = new Task<AuthResult>() {
            @Override
            protected AuthResult call() throws Exception {
                return AuthQuery.getInstance().register(
                        username.getText(),
                        password.getText(),
                        confirmPassword.getText(),
                        firstname.getText(),
                        lastname.getText(),
                        gender.getValue(),
                        dateOfBirth.getValue()
                );
            }
        };
        Common.disable(submitBtn);
        Common.enable(loader);
        task.setOnSucceeded((e) -> {
            Common.disable(loader);
            Common.enable(submitBtn);
            AuthResult authResult = task.getValue();
            registerMessageLabel.setText(authResult.getMessage());
            if (authResult.getUser() != null) {
                safeSwitchScene("login.fxml");
                stage.show();
            }
        });
        new Thread(task).start();
    }

    public void loginButtonOnAction(ActionEvent event) {
        safeSwitchScene("login.fxml");
        stage.show();
    }
}
