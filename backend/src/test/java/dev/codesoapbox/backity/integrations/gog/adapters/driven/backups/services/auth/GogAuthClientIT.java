package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.auth;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import dev.codesoapbox.backity.integrations.gog.domain.model.auth.remote.GogAuthenticationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
class GogAuthClientIT {

    private static final String CLIENT_ID = "someClientId";
    private static final String CLIENT_SECRET = "someClientSecret";

    private GogAuthClient gogAuthClient;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
        var webClientAuth = WebClient.builder()
                .baseUrl(wmRuntimeInfo.getHttpBaseUrl())
                .build();

        gogAuthClient = new GogAuthClient(webClientAuth, CLIENT_ID, CLIENT_SECRET);
    }

    @Test
    void shouldGetInitialToken() {
        String code = "someCode";

        stubFor(get(urlPathEqualTo("/token"))
                .withQueryParam("client_id", equalTo(CLIENT_ID))
                .withQueryParam("client_secret", equalTo(CLIENT_SECRET))
                .withQueryParam("grant_type", equalTo(GogAuthClient.GRANT_TYPE_AUTHORIZATION_CODE))
                .withQueryParam("redirect_uri", equalTo(GogAuthClient.REDIRECT_URI))
                .withQueryParam("code", equalTo(code))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("example_gog_authentication_response.json"))
        );

        var result = gogAuthClient.getInitialToken(code);

        var expectedResult = new GogAuthenticationResponse("someAccessToken", "someRefreshToken",
                3600, "someSessionId", "someUserId");

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldRefreshToken() {
        String refreshToken = "someCode";

        stubFor(get(urlPathEqualTo("/token"))
                .withQueryParam("client_id", equalTo(CLIENT_ID))
                .withQueryParam("client_secret", equalTo(CLIENT_SECRET))
                .withQueryParam("grant_type", equalTo(GogAuthClient.GRANT_TYPE_REFRESH_TOKEN))
                .withQueryParam("refresh_token", equalTo(refreshToken))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("example_gog_authentication_response.json"))
        );

        var result = gogAuthClient.refreshToken(refreshToken);

        var expectedResult = new GogAuthenticationResponse("someAccessToken", "someRefreshToken",
                3600, "someSessionId", "someUserId");

        assertThat(result).isEqualTo(expectedResult);
    }
}