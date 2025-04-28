package dev.codesoapbox.backity.integrations.gog.infrastructure.adapters.driven.backups.services.auth;

import dev.codesoapbox.backity.integrations.gog.domain.model.auth.remote.GogAuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

// https://gogapidocs.readthedocs.io/en/latest/index.html
@Slf4j
@RequiredArgsConstructor
public class GogAuthClient {

    static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    private final WebClient webClientAuth;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public GogAuthenticationResponse getInitialToken(String secret) {
        return webClientAuth.get()
                .uri(uriBuilder -> uriBuilder.path("/token")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("grant_type", GRANT_TYPE_AUTHORIZATION_CODE)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("code", secret)
                        .build())
                .retrieve()
                .bodyToMono(GogAuthenticationResponse.class)
                .block();
    }

    @SuppressWarnings("squid:S1192")
    public GogAuthenticationResponse refreshToken(String refreshToken) {
        return webClientAuth.get()
                .uri(uriBuilder -> uriBuilder.path("/token")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("grant_type", GRANT_TYPE_REFRESH_TOKEN)
                        .queryParam("refresh_token", refreshToken)
                        .build())
                .retrieve()
                .bodyToMono(GogAuthenticationResponse.class)
                .block();
    }
}
