package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.librarymanager.data.AuthQuery;
import org.example.librarymanager.data.AuthResult;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends ControllerWrapper {
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
    @FXML
    private Button submitBtn;
    @FXML
    private MFXProgressSpinner progressSpinner;
    @FXML
    private Button test;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressSpinner.setVisible(false);

        test.setOnAction(event -> {
            safeSwitchScene("admin.fxml");
        });
    }

    public void loginButtonOnAction(ActionEvent event) {
        Task<AuthResult> task = new Task<AuthResult>() {
            @Override
            protected AuthResult call() throws Exception {
                return AuthQuery.getInstance().login(username.getText(), password.getText());
            }
        };
        progressSpinner.setVisible(true);
        submitBtn.setVisible(false);

        task.setOnSucceeded((e) -> {
            progressSpinner.setVisible(false);
            submitBtn.setVisible(true);
            AuthResult loginResult = task.getValue();
            loginMessageLabel.setText(loginResult.getMessage());
            if(loginResult.getUser() != null) {
                setUser(loginResult.getUser());
                safeSwitchScene("home.fxml");
                stage.show();
            }
        });
        new Thread(task).start();
    }

    public void registerButtonOnAction(ActionEvent event) {
        safeSwitchScene("register.fxml");
        stage.show();
    }

}
