package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.embed;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import dev.codesoapbox.backity.core.backup.domain.BackupProgress;
import dev.codesoapbox.backity.core.discovery.domain.ProgressInfo;
import dev.codesoapbox.backity.integrations.gog.config.WebClientConfig;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.GameBackupRequestFailedException;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.GameListRequestFailedException;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameFileDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
@ExtendWith(MockitoExtension.class)
class GogEmbedWebClientTest {

    protected static final String ACCESS_TOKEN = "someAccessToken";

    @RegisterExtension
    static final WireMockExtension wireMockEmbed = WireMockExtension.newInstance()
            .options(new WireMockConfiguration()
                    .useChunkedTransferEncoding(Options.ChunkedEncodingPolicy.NEVER)
                    .dynamicPort())
            .build();

    private static Level defaultLogLevel;

    private GogEmbedWebClient gogEmbedClient;

    @Mock
    private GogAuthService authService;

    @BeforeAll
    static void beforeAll() {
        Logger logger = (Logger) LoggerFactory.getLogger(GogEmbedWebClient.class);
        defaultLogLevel = logger.getLevel();
    }


    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(GogEmbedWebClient.class);
        logger.setLevel(defaultLogLevel);
    }

    @BeforeEach
    void setUp() {
        WebClient webClientEmbed = new WebClientConfig().webClientEmbed(WebClient.builder()).mutate()
                .baseUrl(wireMockEmbed.baseUrl())
                .build();

        gogEmbedClient = new GogEmbedWebClient(webClientEmbed, authService);

        when(authService.getAccessToken())
                .thenReturn(ACCESS_TOKEN);
    }

    @Test
    void shouldGetGameDetails(CapturedOutput capturedOutput) {
        Logger logger = (Logger) LoggerFactory.getLogger(GogEmbedWebClient.class);
        logger.setLevel(Level.DEBUG);

        stubGameEndpoint("example_gog_game_details_response.json");
        stubGameFileEndpoint("/downlink/unreal_tournament_2004_ece/en1installer3");

        GameDetailsResponse result = gogEmbedClient.getGameDetails("someGameId");

        var expectedResult = new GameDetailsResponse("Unreal Tournament 2004 Editor's Choice Edition",
                "//images-4.gog.com/ebed1d5546a4fa382d7d36db8aee7f298eac7db3a8dc2f4389120b5b7b3155a9",
                "someCdKey", "someTextInformation", singletonList(new GameFileDetailsResponse(
                "someVersion", "/downlink/unreal_tournament_2004_ece/en1installer3",
                "Unreal Tournament 2004 Editor's Choice Edition (Part 1 of 3)", "1 MB",
                "en1installer3")),
                "someChangelog");

        assertEquals(expectedResult, result);
        assertTrue(capturedOutput.getOut().contains(
                "Retrieved game details for game: Unreal Tournament 2004 Editor's Choice Edition (#someGameId)"));
    }

    private static void stubGameEndpoint(String responseJson) {
        wireMockEmbed.stubFor(get("/account/gameDetails/someGameId.json")
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile(responseJson)));
    }

    private static void stubGameFileEndpoint(String url) {
        wireMockEmbed.stubFor(head(urlPathEqualTo(url))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Content-Disposition", "filename=setup.exe")));
    }

    @Test
    void shouldGetGameDetailsWhenVersionIsNull() {
        Logger logger = (Logger) LoggerFactory.getLogger(GogEmbedWebClient.class);
        logger.setLevel(Level.DEBUG);

        stubGameEndpoint("example_gog_game_details_response_null_version.json");
        stubGameFileEndpoint("/downlink/unreal_tournament_2004_ece/en1installer3");

        GameDetailsResponse result = gogEmbedClient.getGameDetails("someGameId");

        var expectedResult = new GameDetailsResponse("Unreal Tournament 2004 Editor's Choice Edition",
                "//images-4.gog.com/ebed1d5546a4fa382d7d36db8aee7f298eac7db3a8dc2f4389120b5b7b3155a9",
                "someCdKey", "someTextInformation", singletonList(new GameFileDetailsResponse(
                "unknown", "/downlink/unreal_tournament_2004_ece/en1installer3",
                "Unreal Tournament 2004 Editor's Choice Edition (Part 1 of 3)", "1 MB",
                "en1installer3")),
                "someChangelog");

        assertEquals(expectedResult, result);
    }

    @Test
    void shouldGetGameDetailsWhenDownloadsAreNull() {
        wireMockEmbed.stubFor(get(urlPathEqualTo("/account/gameDetails/someGameId.json"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("example_gog_game_details_response_null_downloads.json")));

        GameDetailsResponse result = gogEmbedClient.getGameDetails("someGameId");

        var expectedResult = new GameDetailsResponse("Unreal Tournament 2004 Editor's Choice Edition",
                "//images-4.gog.com/ebed1d5546a4fa382d7d36db8aee7f298eac7db3a8dc2f4389120b5b7b3155a9",
                "someCdKey", "someTextInformation", emptyList(), "someChangelog");

        assertEquals(expectedResult, result);
    }

    @Test
    void getGameDetailsShouldNotThrowWhenRequestFails(CapturedOutput capturedOutput) {
        wireMockEmbed.stubFor(get(urlPathEqualTo("/account/gameDetails/someGameId.json"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withStatus(500)));

        GameDetailsResponse result = gogEmbedClient.getGameDetails("someGameId");

        assertNull(result);
        assertTrue(capturedOutput.getOut().contains("Could not retrieve game details for game id"));
    }

    @Test
    void getGameDetailsShouldNotThrowWhenRequestReturnsNull(CapturedOutput capturedOutput) {
        wireMockEmbed.stubFor(get(urlPathEqualTo("/account/gameDetails/someGameId.json"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")));

        GameDetailsResponse result = gogEmbedClient.getGameDetails("someGameId");

        assertNull(result);
        assertTrue(capturedOutput.getOut().contains("Response was empty"));
    }

    @Test
    void shouldGetLibrarySize() {
        wireMockEmbed.stubFor(get(urlPathEqualTo("/user/data/games"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("example_gog_game_ids_response.json")));

        wireMockEmbed.stubFor(get(urlPathEqualTo("/account/gameDetails/1.json"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("example_gog_game_details_response.json")));

        wireMockEmbed.stubFor(get(urlPathEqualTo("/account/gameDetails/2.json"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("example_gog_game_details_response.json")));

        stubGameFileEndpoint("/downlink/unreal_tournament_2004_ece/en1installer3");

        var result = gogEmbedClient.getLibrarySize();
        assertEquals("2000000 bytes", result);
    }

    @Test
    void shouldGetLibraryGameIds() {
        var expectedIds = List.of("1", "2");

        wireMockEmbed.stubFor(get(urlPathEqualTo("/user/data/games"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("example_gog_game_ids_response.json")));

        var result = gogEmbedClient.getLibraryGameIds();

        assertEquals(expectedIds, result);
    }

    @Test
    void getLibraryGameIdsShouldReturnEmptyListIfGameIdsAreNull() {
        wireMockEmbed.stubFor(get(urlPathEqualTo("/user/data/games"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")));

        var result = gogEmbedClient.getLibraryGameIds();

        assertTrue(result.isEmpty());
    }

    @Test
    void getLibraryGameIdsShouldThrowIfRequestFails() {
        wireMockEmbed.stubFor(get(urlPathEqualTo("/user/data/games"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withStatus(500)));

        assertThrows(GameListRequestFailedException.class, () -> gogEmbedClient.getLibraryGameIds());
    }

    @Test
    void shouldGetFileBufferWithWorkingRedirectsUpdatingTargetFileNameAndSizeAndProgress() {
        var expectedResult = "abcd";
        var expectedFileName = "someFile.exe";
        var ACCESS_TOKEN = "someAccessToken";
        var targetFileName = new AtomicReference<String>();
        var progress = new BackupProgress();
        var progressHistory = new ArrayList<ProgressInfo>();
        var outputStream = new ByteArrayOutputStream();
        progress.subscribeToProgress(progressHistory::add);

        wireMockEmbed.stubFor(get(urlPathEqualTo("/someUrl1"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Location", "/someUrl2")
                        .withStatus(302)));

        wireMockEmbed.stubFor(get(urlPathEqualTo("/someUrl2"))
                .willReturn(aResponse()
                        .withHeader("Location", "/someUrl3/" + expectedFileName)
                        .withStatus(302)));

        wireMockEmbed.stubFor(get(urlPathEqualTo("/someUrl3/" + expectedFileName))
                .willReturn(aResponse()
                        .withBody(expectedResult.getBytes(StandardCharsets.UTF_8))
                        .withStatus(200)));

        Flux<DataBuffer> dataBufferFlux = gogEmbedClient
                .getFileBuffer("/someUrl1", progress);
        DataBufferUtils.write(dataBufferFlux, progress.getTrackedOutputStream(outputStream)).blockFirst();

        String result = outputStream.toString();

        assertEquals(expectedResult, result);
        assertEquals(4, progress.getContentLengthBytes());
        assertEquals(1, progressHistory.size());
        assertEquals(100, progressHistory.get(0).percentage());
    }

    @Test
    void shouldGetFileBufferWithWorkingRedirectsUpdatingTargetFileNameAndSizeAndProgressForFinalUrlWithQueryParams() {
        var expectedResult = "abcd";
        var expectedFileName = "someFile.exe";
        var progress = new BackupProgress();
        var progressHistory = new ArrayList<ProgressInfo>();
        var outputStream = new ByteArrayOutputStream();
        progress.subscribeToProgress(progressHistory::add);

        wireMockEmbed.stubFor(get(urlPathEqualTo("/someUrl1"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Location", "/someUrl2")
                        .withStatus(302)));

        wireMockEmbed.stubFor(get(urlPathEqualTo("/someUrl2"))
                .willReturn(aResponse()
                        .withHeader("Location", "/someUrl3/" + expectedFileName + "?param=true")
                        .withStatus(302)));

        wireMockEmbed.stubFor(get(urlPathEqualTo("/someUrl3/" + expectedFileName))
                .withQueryParam("param", equalTo("true"))
                .willReturn(aResponse()
                        .withBody(expectedResult.getBytes(StandardCharsets.UTF_8))
                        .withStatus(200)));

        Flux<DataBuffer> dataBufferFlux = gogEmbedClient
                .getFileBuffer("/someUrl1", progress);
        DataBufferUtils.write(dataBufferFlux, progress.getTrackedOutputStream(outputStream)).blockFirst();

        String result = outputStream.toString();

        assertEquals(expectedResult, result);
        assertEquals(4, progress.getContentLengthBytes());
        assertEquals(1, progressHistory.size());
        assertEquals(100, progressHistory.get(0).percentage());
    }

    @Test
    void getFileBufferShouldThrowIfRequestFails() {
        var progress = new BackupProgress();

        wireMockEmbed.stubFor(get(urlPathEqualTo("/someUrl1"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withStatus(500)));

        @SuppressWarnings("ReactiveStreamsUnusedPublisher")
        Flux<DataBuffer> dataBufferFlux = gogEmbedClient
                .getFileBuffer("/someUrl1", progress);

        assertThrows(GameBackupRequestFailedException.class, dataBufferFlux::blockFirst);
    }
}