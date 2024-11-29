package org.example.librarymanager.services;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.Data;

import static org.example.librarymanager.Config.*;

@Data
public class GoogleOAuth2 {
    private static GoogleOAuth2 instance;
    private OAuth20Service service;

    private GoogleOAuth2() {
        service = new ServiceBuilder(OAUTH_CLIENT_ID)
                .apiSecret(OAUTH_CLIENT_SECRET)
                .defaultScope(OAUTH_SCOPE)
                .callback(OAUTH_REDIRECT_URI)
                .build(GoogleApi20.instance());
    }

    public static synchronized GoogleOAuth2 getInstance() {
        if (instance == null) {
            instance = new GoogleOAuth2();
        }
        return instance;
    }
}
