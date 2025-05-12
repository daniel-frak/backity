package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.auth;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.auth.model.remote.GogAuthenticationResponse;
import dev.codesoapbox.backity.testing.wiremock.CustomWireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

class GogAuthClientIT {

    @RegisterExtension
    static final WireMockExtension wireMockEmbed = CustomWireMockExtension.newInstance();

    private static final String CLIENT_ID = "someClientId";
    private static final String CLIENT_SECRET = "someClientSecret";
    private static final String REDIRECT_URI = "someRedirectUri";

    private GogAuthClient gogAuthClient;

    @BeforeEach
    void setUp() {
        configureFor(wireMockEmbed.getRuntimeInfo().getWireMock());
        var webClientAuth = WebClient.builder()
                .baseUrl(wireMockEmbed.getRuntimeInfo().getHttpBaseUrl())
                .build();

        gogAuthClient = new GogAuthClient(webClientAuth, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI);
    }

    @Test
    void shouldGetInitialToken() {
        String code = "someCode";

        stubFor(get(urlPathEqualTo("/token"))
                .withQueryParam("client_id", equalTo(CLIENT_ID))
                .withQueryParam("client_secret", equalTo(CLIENT_SECRET))
                .withQueryParam("grant_type", equalTo(GogAuthClient.GRANT_TYPE_AUTHORIZATION_CODE))
                .withQueryParam("redirect_uri", equalTo(REDIRECT_URI))
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