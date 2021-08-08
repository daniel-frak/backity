package dev.codesoapbox.backity.integrations.gog.application.services.auth;

import dev.codesoapbox.backity.integrations.gog.application.dto.auth.remote.GogAuthenticationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

// https://gogapidocs.readthedocs.io/en/latest/index.html
@Slf4j
@Service
public class GogAuthClient {

    private static final String CLIENT_ID = "46899977096215655";
    private static final String CLIENT_SECRET = "9d85c43b1482497dbbce61f6e4aa173a433796eeae2ca8c5f6129f2dc4de46d9";
    private static final String REDIRECT_URI = "https://embed.gog.com/on_login_success?origin=client";
    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
    private final WebClient webClientAuth;

    public GogAuthClient(@Qualifier("gogAuth") WebClient webClientAuth) {
        this.webClientAuth = webClientAuth;
    }

    public GogAuthenticationResponse refreshToken(String refreshToken) {
        return webClientAuth.get()
                .uri(uriBuilder -> uriBuilder.path("/token")
                        .queryParam("client_id", CLIENT_ID)
                        .queryParam("client_secret", CLIENT_SECRET)
                        .queryParam("grant_type", GRANT_TYPE_REFRESH_TOKEN)
                        .queryParam("refresh_token", refreshToken)
                        .build())
                .retrieve()
                .bodyToMono(GogAuthenticationResponse.class)
                .block();
    }

    public GogAuthenticationResponse getInitialToken(String secret) {
        return webClientAuth.get()
                .uri(uriBuilder -> uriBuilder.path("/token")
                        .queryParam("client_id", CLIENT_ID)
                        .queryParam("client_secret", CLIENT_SECRET)
                        .queryParam("grant_type", GRANT_TYPE_AUTHORIZATION_CODE)
                        .queryParam("redirect_uri", REDIRECT_URI)
                        .queryParam("code", secret)
                        .build())
                .retrieve()
                .bodyToMono(GogAuthenticationResponse.class)
                .block();
    }
}
