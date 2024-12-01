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
import org.example.librarymanager.Config;
import org.example.librarymanager.data.AuthQuery;
import org.example.librarymanager.data.AuthResult;
import org.example.librarymanager.services.GoogleOAuth2;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
    private MFXTextField email;
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
        webView.getEngine().locationProperty().addListener((observableValue, oldLocation, newLocation) -> {
            if (newLocation.startsWith(Config.OAUTH_REDIRECT_URI)) {
                String authCode = URLDecoder.decode(newLocation.split("code=")[1].split("&")[0], StandardCharsets.UTF_8);
                handleOAuthCallback(authCode);
            }
        });
        googleBtn.setOnAction(e -> {
            GoogleOAuth2.getInstance().openOAuth(webView, webViewCloseBtn, this::handleOAuthCallback);
        });
    }

    /**
     * handle oauth callback
     * @param authCode
     */
    private void handleOAuthCallback(String authCode) {
        Task<AuthResult> task = new Task<AuthResult>() {
            @Override
            protected AuthResult call() throws Exception {
                JSONObject response = new JSONObject(GoogleOAuth2.getInstance().handleCallback(authCode));
                return AuthQuery.getInstance().registerWithGoogle(
                        response.getString("email"),
                        response.getString("given_name"),
                        response.getString("family_name"),
                        URLDecoder.decode(response.getString("picture"), StandardCharsets.UTF_8)
                );
            }
        };
        task.setOnSucceeded(e -> {
            Common.disable(webView);
            Common.disable(webViewCloseBtn);
            AuthResult result = task.getValue();
            if (result.getUser() != null) {
                setUser(result.getUser());
                safeSwitchScene("home.fxml");
            } else {
                registerMessageLabel.setText(result.getMessage());
                registerMessageLabel.getStyleClass().clear();
                registerMessageLabel.getStyleClass().add("form-message--error");
            }
        });
        new Thread(task).start();
    }

    /**
     * handle register button click event
     * @param event
     */
    public void registerButtonOnAction(ActionEvent event) {
        Task<AuthResult> task = new Task<AuthResult>() {
            @Override
            protected AuthResult call() throws Exception {
                return AuthQuery.getInstance().register(
                        email.getText(),
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

    /**
     * redirect to log in page
     * @param event
     */
    public void loginButtonOnAction(ActionEvent event) {
        safeSwitchScene("login.fxml");
        stage.show();
    }
}
