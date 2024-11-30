package org.example.librarymanager.services;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import io.undertow.Undertow;
import io.undertow.util.Headers;
import javafx.scene.Node;
import javafx.scene.web.WebView;
import lombok.Data;
import org.example.librarymanager.Common;

import java.awt.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.example.librarymanager.Config.*;

@Data
public class GoogleOAuth2 {
    @FunctionalInterface
    public interface OAuthCallback {
        void execute(String authCode);
    }

    private static GoogleOAuth2 instance;
    private OAuth20Service service;
    private Map<String, String> additionalParams;
    private String secretState;

    private GoogleOAuth2() {
        service = new ServiceBuilder(OAUTH_CLIENT_ID)
                .apiSecret(OAUTH_CLIENT_SECRET)
                .defaultScope(OAUTH_SCOPE)
                .callback(OAUTH_REDIRECT_URI)
                .build(GoogleApi20.instance());
        additionalParams = new HashMap<>();
        additionalParams.put("access_type", "offline");
        additionalParams.put("prompt", "consent");
        secretState = "secret" + new Random().nextInt(999_999);
    }

    public static synchronized GoogleOAuth2 getInstance() {
        if (instance == null) {
            instance = new GoogleOAuth2();
        }
        return instance;
    }

    public String handleCallback(String authCode) {
        try {
            OAuth2AccessToken accessToken = service.getAccessToken(authCode);
            OAuthRequest request = new OAuthRequest(Verb.GET, OAUTH_USER_INFO_ENDPOINT);
            service.signRequest(accessToken, request);
            Response response = service.execute(request);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void openOAuth(WebView webView, Node closeBtn, OAuthCallback callback) {
        String authorizeUrl = service.createAuthorizationUrlBuilder()
                .additionalParams(GoogleOAuth2.getInstance().getAdditionalParams())
                .state(GoogleOAuth2.getInstance().getSecretState())
                .build();
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(authorizeUrl));
                Undertow server = Undertow.builder().addHttpListener(8080, "localhost")
                        .setHandler(exchange -> {
                            if (exchange.getRequestPath().equals("/callback")) {
                                String authCode = exchange.getQueryParameters().get("code").getFirst();
                                callback.execute(authCode);
                                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                                exchange.getResponseSender().send("Authorization successful! You can close this window and go back to your app.");
                            } else {
                                exchange.setStatusCode(404);
                                exchange.getResponseSender().send("Invalid endpoint.!");
                            }
                        }).build();
                server.start();
            } else {
                webView.getEngine().load(authorizeUrl);
                Common.enable(webView);
                Common.enable(closeBtn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
