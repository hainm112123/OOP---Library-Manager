package org.example.librarymanager.app;

import com.github.scribejava.core.oauth.OAuth20Service;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
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
    @FXML
    private WebView webView;
    @FXML
    private MFXButton googleBtn;
    @FXML
    private VBox webViewCloseBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Common.disable(progressSpinner);
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
                Task<AuthResult> task = new Task<AuthResult>() {
                    @Override
                    protected AuthResult call() throws Exception {
                        JSONObject response = new JSONObject(GoogleOAuth2.getInstance().handleCallback(authCode));
                        return AuthQuery.getInstance().loginWithGoogle(response.getString("email"));
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
                        loginMessageLabel.setText(result.getMessage());
                        loginMessageLabel.getStyleClass().clear();
                        loginMessageLabel.getStyleClass().add("form-message--error");
                    }
                });
                new Thread(task).start();
            }
        });
        googleBtn.setOnAction(e -> {
            OAuth20Service service = GoogleOAuth2.getInstance().getService();
            String authorizeUrl = service.getAuthorizationUrl();
            webView.getEngine().load(authorizeUrl);
            Common.enable(webView);
            Common.enable(webViewCloseBtn);
        });
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
