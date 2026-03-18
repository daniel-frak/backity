package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import dev.codesoapbox.backity.core.discovery.domain.exceptions.FileDiscoveryException;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.config.GogProperties;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.config.GogWebClientConfig;
import dev.codesoapbox.backity.testing.wiremock.CustomWireMockExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class GetGameDetailsGogEmbedOperationIT {

    @RegisterExtension
    private static final WireMockExtension wireMock = CustomWireMockExtension.newInstance();

    private static final String ACCESS_TOKEN = "someAccessToken";
    private static final String AUTH_HEADER_VALUE = "Bearer " + ACCESS_TOKEN;
    private static Level defaultLogLevel;

    private final JsonMapper jsonMapper = new JsonMapper();
    private GetGameDetailsGogEmbedOperation operation;

    @Mock
    private GogAuthService authService;

    @BeforeAll
    static void beforeAll() {
        Logger logger = (Logger) LoggerFactory.getLogger(GetGameDetailsGogEmbedOperation.class);
        defaultLogLevel = logger.getLevel();
    }

    @BeforeEach
    void setUp() {
        WebClient webClient = buildWebClient();

        operation = new GetGameDetailsGogEmbedOperation(webClient, authService, jsonMapper);
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

    private void setLogLevelToDebug() {
        Logger logger = (Logger) LoggerFactory.getLogger(GetGameDetailsGogEmbedOperation.class);
        logger.setLevel(Level.DEBUG);
    }

    private void stubGameEndpoint(String responseJson) {
        wireMock.stubFor(get("/account/gameDetails/someGameId.json")
                .withHeader(GogEmbedHeaders.AUTHORIZATION, equalTo(AUTH_HEADER_VALUE))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile(responseJson)));
    }

    private void stubFileEndpoint(String url) {
        wireMock.stubFor(head(urlEqualTo(url))
                .withHeader(GogEmbedHeaders.AUTHORIZATION, equalTo(AUTH_HEADER_VALUE))
                .willReturn(aResponse()));
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(GetGameDetailsGogEmbedOperation.class);
        logger.setLevel(defaultLogLevel);
    }

    @Nested
    class Successful {

        @Test
        void shouldGetGameDetails(CapturedOutput capturedOutput) {
            setLogLevelToDebug();
            stubGameEndpoint("example_gog_game_details_response.json");
            stubFileEndpoint("/downlink/unreal_tournament_2004_ece/en1installer3");

            Optional<GogGameWithFiles> result = operation.execute("someGameId");

            var expectedResult = new GogGameWithFiles("Unreal Tournament 2004 Editor's Choice Edition",
                    "//images-4.gog.com/ebed1d5546a4fa382d7d36db8aee7f298eac7db3a8dc2f4389120b5b7b3155a9",
                    "some-cd-key", "someTextInformation", singletonList(new GogFile(
                    "1.0.0", "/downlink/unreal_tournament_2004_ece/en1installer3",
                    "Unreal Tournament 2004 Editor's Choice Edition (Part 1 of 3)", "1 MB",
                    "en1installer3")),
                    "someChangelog");
            assertThat(result).get().isEqualTo(expectedResult);
            assertThat(capturedOutput.getOut()).contains(
                    "Retrieved game details for game: Unreal Tournament 2004 Editor's" +
                            " Choice Edition (#someGameId)");
        }

        @Test
        void shouldExtractFileTitleGivenFinalLocationUrlIncludesQueryParams() {
            stubGameEndpoint("example_gog_game_details_response_manual_url_with_query_params.json");
            stubFileEndpoint("/downlink/unreal_tournament_2004_ece/en1installer3?some-query-param=true");

            Optional<GogGameWithFiles> result = operation.execute("someGameId");

            assertThat(result).get().extracting(r -> r.files().getFirst().fileName())
                    .isEqualTo("en1installer3");
        }

        @Test
        void shouldGetGameDetailsGivenVersionIsNull() {
            setLogLevelToDebug();
            stubGameEndpoint("example_gog_game_details_response_null_version.json");
            stubFileEndpoint("/downlink/unreal_tournament_2004_ece/en1installer3");

            Optional<GogGameWithFiles> result = operation.execute("someGameId");

            var expectedResult = new GogGameWithFiles("Unreal Tournament 2004 Editor's Choice Edition",
                    "//images-4.gog.com/ebed1d5546a4fa382d7d36db8aee7f298eac7db3a8dc2f4389120b5b7b3155a9",
                    "some-cd-key", "someTextInformation", singletonList(new GogFile(
                    "unknown", "/downlink/unreal_tournament_2004_ece/en1installer3",
                    "Unreal Tournament 2004 Editor's Choice Edition (Part 1 of 3)", "1 MB",
                    "en1installer3")),
                    "someChangelog");
            assertThat(result).get().isEqualTo(expectedResult);
        }

        @Test
        void shouldGetGameDetailsGivenDownloadsAreNull() {
            wireMock.stubFor(get(urlPathEqualTo("/account/gameDetails/someGameId.json"))
                    .withHeader(GogEmbedHeaders.AUTHORIZATION, equalTo(AUTH_HEADER_VALUE))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBodyFile("example_gog_game_details_response_null_downloads.json")));

            Optional<GogGameWithFiles> result = operation.execute("someGameId");

            var expectedResult = new GogGameWithFiles("Unreal Tournament 2004 Editor's Choice Edition",
                    "//images-4.gog.com/ebed1d5546a4fa382d7d36db8aee7f298eac7db3a8dc2f4389120b5b7b3155a9",
                    "some-cd-key", "someTextInformation", emptyList(), "someChangelog");
            assertThat(result).get().isEqualTo(expectedResult);
        }
    }

    @Nested
    class Throwing {

        private static Stream<Arguments> invalidResponses() {
            return Stream.of(
                    Arguments.of(
                            Named.of(
                                    "unprocessable",
                                    "{\"extras\": 123}"
                            ),
                            "Could not retrieve game details for game id"
                    ),
                    Arguments.of(
                            /*
                             * GOG sometimes returns `[]` from this endpoint.
                             * It seems to happen when the 'game' is a bundle (product type: package).
                             */
                            Named.of(
                                    "empty array with whitespace",
                                    "[ ]"
                            ),
                            "Response was empty"
                    ),
                    Arguments.of(
                            // Not currently returned by GOG, but feels prudent to guard against.
                            Named.of(
                                    "whitespace",
                                    " "
                            ),
                            "Response was empty"
                    )
            );
        }

        @Test
        void shouldThrowGivenResponseFinalLocationHeaderIsBlank() {
            setLogLevelToDebug();
            stubGameEndpoint("example_gog_game_details_response.json");

            wireMock.stubFor(head(urlPathEqualTo("/downlink/unreal_tournament_2004_ece/en1installer3"))
                    .withHeader(GogEmbedHeaders.AUTHORIZATION, equalTo(AUTH_HEADER_VALUE))
                    .willReturn(aResponse()
                            .withHeader("Final-location", "")));

            assertThatThrownBy(() -> operation.execute("someGameId"))
                    .isInstanceOf(FileDiscoveryException.class)
                    .hasMessage("Could not extract file title from response");
        }

        @Test
        void shouldNotThrowGivenRequestFails(CapturedOutput capturedOutput) {
            wireMock.stubFor(get(urlPathEqualTo("/account/gameDetails/someGameId.json"))
                    .withHeader(GogEmbedHeaders.AUTHORIZATION, equalTo(AUTH_HEADER_VALUE))
                    .willReturn(aResponse()
                            .withStatus(500)));

            Optional<GogGameWithFiles> result = operation.execute("someGameId");

            assertThat(result).isEmpty();
            assertThat(capturedOutput.getOut()).contains("Could not retrieve game details for game id");
        }

        @Test
        void shouldNotThrowGivenRequestReturnsNull(CapturedOutput capturedOutput) {
            wireMock.stubFor(get(urlPathEqualTo("/account/gameDetails/someGameId.json"))
                    .withHeader(GogEmbedHeaders.AUTHORIZATION, equalTo(AUTH_HEADER_VALUE))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")));

            Optional<GogGameWithFiles> result = operation.execute("someGameId");

            assertThat(result).isEmpty();
            assertThat(capturedOutput.getOut()).contains("Response was empty");
        }

        @ParameterizedTest(name = "response is {0}")
        @MethodSource("invalidResponses")
        void shouldNotThrowGiven(String responseBody,
                                 String expectedLogMessage,
                                 CapturedOutput capturedOutput) {
            wireMock.stubFor(get(urlPathEqualTo("/account/gameDetails/someGameId.json"))
                    .withHeader(GogEmbedHeaders.AUTHORIZATION, equalTo(AUTH_HEADER_VALUE))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(responseBody)));

            Optional<GogGameWithFiles> result = operation.execute("someGameId");

            assertThat(result).isEmpty();
            assertThat(capturedOutput.getOut()).contains(expectedLogMessage);
        }
    }
}
