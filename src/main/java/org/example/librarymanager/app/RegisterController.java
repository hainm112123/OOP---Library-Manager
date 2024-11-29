package org.example.librarymanager.app;

import com.github.scribejava.core.oauth.OAuth20Service;
import io.github.palexdev.materialfx.controls.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.example.librarymanager.Common;
import org.example.librarymanager.data.AuthQuery;
import org.example.librarymanager.data.AuthResult;
import org.example.librarymanager.services.GoogleOAuth2;

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
    @FXML
    private WebView webView;
    @FXML
    private MFXButton googleBtn;
    @FXML
    private VBox webViewCloseBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gender.getItems().addAll("Male", "Female");
        Common.disable(loader);
        Common.disable(webView);
        Common.disable(webViewCloseBtn);
        webViewCloseBtn.setOnMouseClicked(e -> {
            Common.disable(webView);
            webView.getEngine().load(null);
            Common.disable(webViewCloseBtn);
        });
        googleBtn.setOnAction(e -> {
            OAuth20Service service = GoogleOAuth2.getInstance().getService();
            String authorizeUrl = service.getAuthorizationUrl();
            webView.getEngine().load(authorizeUrl);
            Common.enable(webView);
            Common.enable(webViewCloseBtn);
        });
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
                registerMessageLabel.getStyleClass().clear();
                registerMessageLabel.getStyleClass().add("success-message--error");
                safeSwitchScene("login.fxml");
                stage.show();
            } else {
                registerMessageLabel.getStyleClass().clear();
                registerMessageLabel.getStyleClass().add("form-message--error");
            }
        });
        new Thread(task).start();
    }

    public void loginButtonOnAction(ActionEvent event) {
        safeSwitchScene("login.fxml");
        stage.show();
    }
}
