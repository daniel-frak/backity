package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.exceptions.GameListRequestFailedException;
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

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLibraryGameIdsGogEmbedOperationIT {

    @RegisterExtension
    private static final WireMockExtension wireMock = CustomWireMockExtension.newInstance();

    private static final String ACCESS_TOKEN = "someAccessToken";
    private static final String AUTH_HEADER_VALUE = "Bearer " + ACCESS_TOKEN;

    private GetLibraryGameIdsGogEmbedOperation operation;

    @Mock
    private GogAuthService authService;

    @BeforeEach
    void setUp() {
        WebClient webClient = buildWebClient();

        operation = new GetLibraryGameIdsGogEmbedOperation(webClient, authService);

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

    private void stubGameIdsEndpoint() {
        wireMock.stubFor(get(urlPathEqualTo("/user/data/games"))
                .withHeader(GogEmbedHeaders.AUTHORIZATION, equalTo(AUTH_HEADER_VALUE))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("example_gog_game_ids_response.json")));
    }

    @Nested
    class Successful {

        @Test
        void shouldReturnLibraryGameIds() {
            var expectedIds = List.of("1", "2");
            stubGameIdsEndpoint();

            var result = operation.execute();

            assertThat(result).isEqualTo(expectedIds);
        }

        @Test
        void shouldReturnEmptyListGivenGameIdsAreNull() {
            stubGameIdsEndpointWithEmptyResponse();

            var result = operation.execute();

            assertThat(result).isEmpty();
        }

        private void stubGameIdsEndpointWithEmptyResponse() {
            wireMock.stubFor(get(urlPathEqualTo("/user/data/games"))
                    .withHeader(GogEmbedHeaders.AUTHORIZATION, equalTo(AUTH_HEADER_VALUE))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")));
        }
    }

    @Nested
    class Throwing {

        @Test
        void shouldThrowGivenRequestFails() {
            stubGameIdsEndpointReturningHttp500();

            assertThatThrownBy(() -> operation.execute())
                    .isInstanceOf(GameListRequestFailedException.class);
        }

        private void stubGameIdsEndpointReturningHttp500() {
            wireMock.stubFor(get(urlPathEqualTo("/user/data/games"))
                    .withHeader(GogEmbedHeaders.AUTHORIZATION, equalTo(AUTH_HEADER_VALUE))
                    .willReturn(aResponse()
                            .withStatus(500)));
        }
    }
}
