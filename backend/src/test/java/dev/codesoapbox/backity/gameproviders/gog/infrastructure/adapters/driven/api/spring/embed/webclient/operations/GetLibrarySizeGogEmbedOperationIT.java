package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.config.GogProperties;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.config.GogWebClientConfig;
import dev.codesoapbox.backity.testing.wiremock.CustomWireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.json.JsonMapper;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLibrarySizeGogEmbedOperationIT {

    @RegisterExtension
    private static final WireMockExtension wireMock = CustomWireMockExtension.newInstance();

    private static final String ACCESS_TOKEN = "someAccessToken";
    private static final String AUTH_HEADER_VALUE = "Bearer " + ACCESS_TOKEN;

    private final JsonMapper jsonMapper = new JsonMapper();
    private GetLibrarySizeGogEmbedOperation operation;

    @Mock
    private GogAuthService authService;

    @BeforeEach
    void setUp() {
        WebClient webClient = buildWebClient();

        operation = new GetLibrarySizeGogEmbedOperation(
                new GetLibraryGameIdsGogEmbedOperation(webClient, authService),
                new GetGameDetailsGogEmbedOperation(webClient, authService, jsonMapper)
        );
        authServiceProvidesAccessToken();
    }

    private WebClient buildWebClient() {
        var gogProperties = new GogProperties(
                null,
                null,
                null,
                new GogProperties.EmbedProperties(wireMock.baseUrl())
        );

        return new GogWebClientConfig(gogProperties).webClientEmbed(WebClient.builder());
    }

    private void authServiceProvidesAccessToken() {
        when(authService.getAccessToken())
                .thenReturn(ACCESS_TOKEN);
    }

    @Nested
    class Successful {

        @Test
        void shouldGetLibrarySize() {
            stubGameIdsEndpoint();
            stubGameDetailsEndpoint("1");
            stubGameDetailsEndpoint("2");
            stubFileEndpoint("/downlink/unreal_tournament_2004_ece/en1installer3");

            var result = operation.execute();
            assertThat(result).isEqualTo("2 MB");
        }

        private void stubGameIdsEndpoint() {
            wireMock.stubFor(get(urlPathEqualTo("/user/data/games"))
                    .withHeader(GogEmbedHeaders.AUTHORIZATION, equalTo(AUTH_HEADER_VALUE))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBodyFile("example_gog_game_ids_response.json")));
        }

        private void stubGameDetailsEndpoint(String gameId) {
            wireMock.stubFor(get(urlPathEqualTo("/account/gameDetails/" + gameId + ".json"))
                    .withHeader(GogEmbedHeaders.AUTHORIZATION,
                            equalTo("Bearer " + ACCESS_TOKEN))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBodyFile("example_gog_game_details_response.json")));
        }

        @SuppressWarnings("SameParameterValue")
        private void stubFileEndpoint(String url) {
            wireMock.stubFor(head(urlEqualTo(url))
                    .withHeader(GogEmbedHeaders.AUTHORIZATION, equalTo(AUTH_HEADER_VALUE))
                    .willReturn(aResponse()));
        }
    }
}