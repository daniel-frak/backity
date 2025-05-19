package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.ProgressInfo;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import dev.codesoapbox.backity.core.gamefile.domain.TestFileSource;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.exceptions.FileDiscoveryException;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.exceptions.GameBackupRequestFailedException;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.exceptions.GameListRequestFailedException;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.config.GogProperties;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.config.GogWebClientConfig;
import dev.codesoapbox.backity.testing.wiremock.CustomWireMockExtension;
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
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
@ExtendWith(MockitoExtension.class)
class GogEmbedWebClientIT {

    @RegisterExtension
    static final WireMockExtension wireMockEmbed = CustomWireMockExtension.newInstance();

    private static final String ACCESS_TOKEN = "someAccessToken";

    private static Level defaultLogLevel;

    private GogEmbedWebClient gogEmbedClient;

    @Mock
    private GogAuthService authService;

    @Mock
    private Clock clock;

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
        var gogProperties = new GogProperties(
                null,
                null,
                null,
                new GogProperties.EmbedProperties(wireMockEmbed.baseUrl())
        );
        WebClient webClientEmbed = new GogWebClientConfig(gogProperties).webClientEmbed(WebClient.builder());

        gogEmbedClient = new GogEmbedWebClient(webClientEmbed, authService, clock);

        when(authService.getAccessToken())
                .thenReturn(ACCESS_TOKEN);
    }

    @Test
    void shouldGetGameDetails(CapturedOutput capturedOutput) {
        setLogLevelToDebug();
        stubGameEndpoint("example_gog_game_details_response.json");
        stubFileEndpoint("/downlink/unreal_tournament_2004_ece/en1installer3");

        GogGameWithFiles result = gogEmbedClient.getGameDetails("someGameId");

        var expectedResult = new GogGameWithFiles("Unreal Tournament 2004 Editor's Choice Edition",
                "//images-4.gog.com/ebed1d5546a4fa382d7d36db8aee7f298eac7db3a8dc2f4389120b5b7b3155a9",
                "some-cd-key", "someTextInformation", singletonList(new GogGameFile(
                "1.0.0", "/downlink/unreal_tournament_2004_ece/en1installer3",
                "Unreal Tournament 2004 Editor's Choice Edition (Part 1 of 3)", "1 MB",
                "en1installer3")),
                "someChangelog");
        assertThat(result).isEqualTo(expectedResult);
        assertThat(capturedOutput.getOut()).contains(
                "Retrieved game details for game: Unreal Tournament 2004 Editor's" +
                " Choice Edition (#someGameId)");
    }

    private void setLogLevelToDebug() {
        Logger logger = (Logger) LoggerFactory.getLogger(GogEmbedWebClient.class);
        logger.setLevel(Level.DEBUG);
    }

    private void stubGameEndpoint(String responseJson) {
        wireMockEmbed.stubFor(get("/account/gameDetails/someGameId.json")
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile(responseJson)));
    }

    private void stubFileEndpoint(String url) {
        wireMockEmbed.stubFor(head(urlPathEqualTo(url))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()));
    }

    @Test
    void shouldExtractFileTitleGivenFinalLocationUrlIncludesQueryParams() {
        stubGameEndpoint("example_gog_game_details_response_manual_url_with_query_params.json");
        stubFileEndpoint("/downlink/unreal_tournament_2004_ece/en1installer3?some-query-param=true");

        GogGameWithFiles result = gogEmbedClient.getGameDetails("someGameId");

        assertThat(result.files().getFirst().fileName()).isEqualTo("en1installer3");
    }

    @Test
    void shouldGetGameDetailsWhenVersionIsNull() {
        setLogLevelToDebug();
        stubGameEndpoint("example_gog_game_details_response_null_version.json");
        stubFileEndpoint("/downlink/unreal_tournament_2004_ece/en1installer3");

        GogGameWithFiles result = gogEmbedClient.getGameDetails("someGameId");

        var expectedResult = new GogGameWithFiles("Unreal Tournament 2004 Editor's Choice Edition",
                "//images-4.gog.com/ebed1d5546a4fa382d7d36db8aee7f298eac7db3a8dc2f4389120b5b7b3155a9",
                "some-cd-key", "someTextInformation", singletonList(new GogGameFile(
                "unknown", "/downlink/unreal_tournament_2004_ece/en1installer3",
                "Unreal Tournament 2004 Editor's Choice Edition (Part 1 of 3)", "1 MB",
                "en1installer3")),
                "someChangelog");
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldGetGameDetailsWhenDownloadsAreNull() {
        wireMockEmbed.stubFor(get(urlPathEqualTo("/account/gameDetails/someGameId.json"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("example_gog_game_details_response_null_downloads.json")));

        GogGameWithFiles result = gogEmbedClient.getGameDetails("someGameId");

        var expectedResult = new GogGameWithFiles("Unreal Tournament 2004 Editor's Choice Edition",
                "//images-4.gog.com/ebed1d5546a4fa382d7d36db8aee7f298eac7db3a8dc2f4389120b5b7b3155a9",
                "some-cd-key", "someTextInformation", emptyList(), "someChangelog");
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void getGameDetailsShouldThrowWhenResponseFinalLocationHeaderIsBlank() {
        setLogLevelToDebug();
        stubGameEndpoint("example_gog_game_details_response.json");

        wireMockEmbed.stubFor(head(urlPathEqualTo("/downlink/unreal_tournament_2004_ece/en1installer3"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Final-location", "")));

        assertThatThrownBy(() -> gogEmbedClient.getGameDetails("someGameId"))
                .isInstanceOf(FileDiscoveryException.class)
                .hasMessage("Could not extract file title from response");
    }

    @Test
    void getGameDetailsShouldNotThrowWhenRequestFails(CapturedOutput capturedOutput) {
        wireMockEmbed.stubFor(get(urlPathEqualTo("/account/gameDetails/someGameId.json"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withStatus(500)));

        GogGameWithFiles result = gogEmbedClient.getGameDetails("someGameId");

        assertThat(result).isNull();
        assertThat(capturedOutput.getOut()).contains("Could not retrieve game details for game id");
    }

    @Test
    void getGameDetailsShouldNotThrowWhenRequestReturnsNull(CapturedOutput capturedOutput) {
        wireMockEmbed.stubFor(get(urlPathEqualTo("/account/gameDetails/someGameId.json"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")));

        GogGameWithFiles result = gogEmbedClient.getGameDetails("someGameId");

        assertThat(result).isNull();
        assertThat(capturedOutput.getOut()).contains("Response was empty");
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

        stubFileEndpoint("/downlink/unreal_tournament_2004_ece/en1installer3");

        var result = gogEmbedClient.getLibrarySize();
        assertThat(result).isEqualTo("2 MB");
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

        assertThat(result).isEqualTo(expectedIds);
    }

    @Test
    void getLibraryGameIdsShouldReturnEmptyListIfGameIdsAreNull() {
        wireMockEmbed.stubFor(get(urlPathEqualTo("/user/data/games"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")));

        var result = gogEmbedClient.getLibraryGameIds();

        assertThat(result).isEmpty();
    }

    @Test
    void getLibraryGameIdsShouldThrowIfRequestFails() {
        wireMockEmbed.stubFor(get(urlPathEqualTo("/user/data/games"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withStatus(500)));

        assertThatThrownBy(() -> gogEmbedClient.getLibraryGameIds())
                .isInstanceOf(GameListRequestFailedException.class);
    }

    @Test
    void initializeProgressAndStreamFileShouldStreamFile() {
        var expectedFileContent = "abcd";
        FileSource gogFile = mockAuthenticatedGogFileRetrievalWithoutRedirects(expectedFileContent);
        DownloadProgress progress = new DownloadProgress();

        TrackableFileStream trackableFileStream = gogEmbedClient
                .initializeProgressAndStreamFile(gogFile, progress);
        String fileContent = downloadFile(trackableFileStream, progress);

        assertThat(fileContent).isEqualTo(expectedFileContent);
    }

    private String downloadFile(TrackableFileStream trackableFileStream, DownloadProgress progress) {
        var outputStream = new ByteArrayOutputStream();
        DataBufferUtils.write(trackableFileStream.dataStream(), progress.track(outputStream)).blockFirst();
        return outputStream.toString();
    }

    private FileSource mockAuthenticatedGogFileRetrievalWithoutRedirects(
            String expectedFileContent) {
        FileSource gogFile = aGogFile();
        wireMockEmbed.stubFor(get(urlPathEqualTo(gogFile.url()))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withBody(expectedFileContent.getBytes(StandardCharsets.UTF_8))
                        .withStatus(200)));
        return gogFile;
    }

    private FileSource aGogFile() {
        return TestFileSource.minimalGogBuilder()
                .url("/someUrl1")
                .build();
    }

    @Test
    void initializeProgressAndStreamFileShouldInitializeDownloadProgress() {
        var expectedFileContent = "abcd";
        FileSource gogFile = mockAuthenticatedGogFileRetrievalWithoutRedirects(expectedFileContent);
        var progress = new DownloadProgress();
        List<ProgressInfo> progressHistory = trackProgressHistory(progress);

        TrackableFileStream trackableFileStream = gogEmbedClient
                .initializeProgressAndStreamFile(gogFile, progress);
        downloadFile(trackableFileStream, progress);

        assertProgressWasTrackedCorrectly(progress, progressHistory, expectedFileContent);
    }

    private List<ProgressInfo> trackProgressHistory(DownloadProgress progress) {
        var progressHistory = new ArrayList<ProgressInfo>();
        progress.subscribeToProgress(progressHistory::add);
        return progressHistory;
    }

    private void assertProgressWasTrackedCorrectly(DownloadProgress progress, List<ProgressInfo> progressHistory,
                                                   String expectedFileContent) {
        assertThat(progress.getContentLengthBytes()).isEqualTo(expectedFileContent.getBytes().length);
        assertThat(progressHistory).hasSize(1);
        assertThat(progressHistory.getFirst().percentage()).isEqualTo(100);
    }

    @Test
    void initializeProgressAndStreamFileShouldStreamFileGivenUrlRedirects() {
        var expectedFileContent = "abcd";
        var progress = new DownloadProgress();
        FileSource gogFile =
                mockAuthenticatedGogFileRetrievalWithRedirects(expectedFileContent);

        TrackableFileStream fileStream = gogEmbedClient
                .initializeProgressAndStreamFile(gogFile, progress);
        String fileContent = downloadFile(fileStream, progress);

        assertThat(fileContent).isEqualTo(expectedFileContent);
    }

    private FileSource mockAuthenticatedGogFileRetrievalWithRedirects(
            String expectedFileContent) {
        FileSource gogFile = aGogFile();

        wireMockEmbed.stubFor(get(urlPathEqualTo(gogFile.url()))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Location", gogFile.url() + "-2")
                        .withStatus(302)));
        wireMockEmbed.stubFor(get(urlPathEqualTo(gogFile.url() + "-2"))
                .willReturn(aResponse()
                        .withHeader("Location", gogFile.url() + "-3/")
                        .withStatus(302)));
        wireMockEmbed.stubFor(get(urlPathEqualTo(gogFile.url() + "-3/"))
                .willReturn(aResponse()
                        .withBody(expectedFileContent.getBytes(StandardCharsets.UTF_8))
                        .withStatus(200)));

        return gogFile;
    }

    @Test
    void initializeProgressAndStreamFileShouldStreamFileGivenUrlRedirectsAndHasQueryParams() {
        var expectedFileContent = "abcd";
        var progress = new DownloadProgress();
        FileSource gogFile = mockAuthenticatedGogFileRetrievalWithRedirectsAndQueryParams(expectedFileContent);

        TrackableFileStream fileStream = gogEmbedClient
                .initializeProgressAndStreamFile(gogFile, progress);
        String fileContent = downloadFile(fileStream, progress);

        assertThat(fileContent).isEqualTo(expectedFileContent);
    }

    private FileSource mockAuthenticatedGogFileRetrievalWithRedirectsAndQueryParams(String expectedResult) {
        FileSource gogFile = aGogFile();
        wireMockEmbed.stubFor(get(urlPathEqualTo(gogFile.url()))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withHeader("Location", gogFile.url() + "-2")
                        .withStatus(302)));
        wireMockEmbed.stubFor(get(urlPathEqualTo(gogFile.url() + "-2"))
                .willReturn(aResponse()
                        .withHeader("Location", gogFile.url() + "-3?param=true")
                        .withStatus(302)));
        wireMockEmbed.stubFor(get(urlPathEqualTo(gogFile.url() + "-3"))
                .withQueryParam("param", equalTo("true"))
                .willReturn(aResponse()
                        .withBody(expectedResult.getBytes(StandardCharsets.UTF_8))
                        .withStatus(200)));

        return gogFile;
    }

    @Test
    void initializeProgressAndStreamFileShouldThrowIfRequestFails() {
        FileSource gogFile = aGogFile();
        var progress = new DownloadProgress();
        mockGogFileRetrievalFails(gogFile);

        TrackableFileStream fileStream = gogEmbedClient
                .initializeProgressAndStreamFile(gogFile, progress);

        Flux<DataBuffer> dataBufferFlux = fileStream.dataStream();
        assertThatThrownBy(dataBufferFlux::blockFirst)
                .isInstanceOf(GameBackupRequestFailedException.class);
    }

    private void mockGogFileRetrievalFails(FileSource gogFile) {
        wireMockEmbed.stubFor(get(urlPathEqualTo(gogFile.url()))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(aResponse()
                        .withStatus(500)));
    }
}