package org.example.librarymanager.app;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import org.example.librarymanager.Common;
import org.example.librarymanager.data.AuthQuery;
import org.example.librarymanager.data.AuthResult;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangePasswordController extends ControllerWrapper {
    @FXML
    private MFXPasswordField currentPassword;
    @FXML
    private MFXPasswordField newPassword;
    @FXML
    private MFXPasswordField confirmPassword;
    @FXML
    private MFXButton submitBtn;
    @FXML
    private MFXProgressSpinner loader;
    @FXML
    private Label message;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Common.disable(loader);
        Common.disable(message);
        submitBtn.setRippleColor(Paint.valueOf(Common.PRIMARY_COLOR));
        Common.hideMessage(currentPassword, message);
        Common.hideMessage(newPassword, message);
        Common.hideMessage(confirmPassword, message);
        submitBtn.setOnAction(event -> {
            Common.disable(submitBtn);
            Common.enable(loader);
            Task<AuthResult> task = new Task<AuthResult>() {
                @Override
                protected AuthResult call() throws Exception {
                    return AuthQuery.getInstance().changePassword(getUser(), currentPassword.getText(), newPassword.getText(), confirmPassword.getText());
                }
            };
            task.setOnSucceeded(e -> {
                AuthResult result = task.getValue();
                message.setText(result.getMessage());
                if (result.getUser() != null) {
                    message.getStyleClass().clear();
                    message.getStyleClass().add("form-message--success");
                    currentPassword.clear();
                    newPassword.clear();
                    confirmPassword.clear();
                } else {
                    message.getStyleClass().clear();
                    message.getStyleClass().add("form-message--error");
                }

                Common.enable(message);
                Common.enable(submitBtn);
                Common.disable(loader);
            });
            new Thread(task).start();
        });
    }
}
