package org.example.librarymanager.services;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.example.librarymanager.Config.*;

@Data
public class GoogleOAuth2 {
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
}
