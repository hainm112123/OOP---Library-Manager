package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
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
    private MFXTextField email;
    @FXML
    private MFXPasswordField password;
    @FXML
    private Hyperlink registerHyperlink;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private MFXButton submitBtn;
    @FXML
    private MFXProgressSpinner progressSpinner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressSpinner.setVisible(false);

    }

    public void loginButtonOnAction(ActionEvent event) {
        Task<AuthResult> task = new Task<AuthResult>() {
            @Override
            protected AuthResult call() throws Exception {
                return AuthQuery.getInstance().login(email.getText(), password.getText());
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
                loginMessageLabel.getStyleClass().clear();
                loginMessageLabel.getStyleClass().add("form-message--success");
                setUser(loginResult.getUser());
                safeSwitchScene("home.fxml");
                stage.show();
            } else {
                loginMessageLabel.getStyleClass().clear();
                loginMessageLabel.getStyleClass().add("form-message--error");
            }
        });
        new Thread(task).start();
    }

    public void registerButtonOnAction(ActionEvent event) {
        safeSwitchScene("register.fxml");
        stage.show();
    }

}
