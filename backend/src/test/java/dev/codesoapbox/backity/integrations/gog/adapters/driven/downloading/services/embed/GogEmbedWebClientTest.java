package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.embed;

import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import dev.codesoapbox.backity.core.files.discovery.domain.model.ProgressInfo;
import dev.codesoapbox.backity.core.files.downloading.domain.services.DownloadProgress;
import dev.codesoapbox.backity.integrations.gog.config.WebClientConfig;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.GameDetailsRequestFailedException;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.GameDownloadRequestFailedException;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.GameListRequestFailedException;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameFileDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GogEmbedWebClientTest {

    @RegisterExtension
    private static final WireMockExtension wireMockEmbed = WireMockExtension.newInstance()
            .options(new WireMockConfiguration()
                    .useChunkedTransferEncoding(Options.ChunkedEncodingPolicy.NEVER)
                    .dynamicPort())
            .build();

    private GogEmbedWebClient gogEmbedClient;

    @Mock
    private GogAuthService authService;

    @BeforeEach
    void setUp() {
        WebClient webClientEmbed = new WebClientConfig().webClientEmbed(WebClient.builder()).mutate()
                .baseUrl(wireMockEmbed.baseUrl())
                .build();

        gogEmbedClient = new GogEmbedWebClient(webClientEmbed, authService);
    }

    @Test
    void shouldGetGameDetails() {
        var accessToken = "someAccessToken";

        when(authService.getAccessToken())
                .thenReturn(accessToken);

        wireMockEmbed.stubFor(get(urlPathEqualTo("/account/gameDetails/someGameId.json"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + accessToken))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("example_gog_game_details_response.json")));

        GameDetailsResponse result = gogEmbedClient.getGameDetails("someGameId");

        var expectedResult = new GameDetailsResponse("Unreal Tournament 2004 Editor's Choice Edition",
                "//images-4.gog.com/ebed1d5546a4fa382d7d36db8aee7f298eac7db3a8dc2f4389120b5b7b3155a9",
                "someCdKey", "someTextInformation", singletonList(new GameFileDetailsResponse(
                "someVersion", "/downlink/unreal_tournament_2004_ece/en1installer3",
                "Unreal Tournament 2004 Editor's Choice Edition (Part 1 of 3)", "1 MB")),
                "someChangelog");

        assertEquals(expectedResult, result);
    }

    @Test
    void getGameDetailsShouldThrowWhenRequestFails() {
        var accessToken = "someAccessToken";

        when(authService.getAccessToken())
                .thenReturn(accessToken);

        wireMockEmbed.stubFor(get(urlPathEqualTo("/account/gameDetails/someGameId.json"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + accessToken))
                .willReturn(aResponse()
                        .withStatus(500)));

        assertThrows(GameDetailsRequestFailedException.class, () -> gogEmbedClient.getGameDetails("someGameId"));
    }

    @Test
    void shouldGetLibrarySize() {
        var accessToken = "someAccessToken";

        when(authService.getAccessToken())
                .thenReturn(accessToken);

        wireMockEmbed.stubFor(get(urlPathEqualTo("/user/data/games"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + accessToken))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("example_gog_game_ids_response.json")));

        wireMockEmbed.stubFor(get(urlPathEqualTo("/account/gameDetails/1.json"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + accessToken))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("example_gog_game_details_response.json")));

        wireMockEmbed.stubFor(get(urlPathEqualTo("/account/gameDetails/2.json"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + accessToken))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("example_gog_game_details_response.json")));

        var result = gogEmbedClient.getLibrarySize();
        assertEquals("2000000 bytes", result);
    }

    @Test
    void shouldGetLibraryGameIds() {
        var accessToken = "someAccessToken";
        var expectedIds = List.of("1", "2");

        when(authService.getAccessToken())
                .thenReturn(accessToken);

        wireMockEmbed.stubFor(get(urlPathEqualTo("/user/data/games"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + accessToken))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("example_gog_game_ids_response.json")));

        var result = gogEmbedClient.getLibraryGameIds();

        assertEquals(expectedIds, result);
    }

    @Test
    void getLibraryGameIdsShouldThrowIfRequestFails() {
        var accessToken = "someAccessToken";

        when(authService.getAccessToken())
                .thenReturn(accessToken);

        wireMockEmbed.stubFor(get(urlPathEqualTo("/user/data/games"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + accessToken))
                .willReturn(aResponse()
                        .withStatus(500)));

        assertThrows(GameListRequestFailedException.class, () -> gogEmbedClient.getLibraryGameIds());
    }

    @Test
    void shouldGetFileBufferWithWorkingRedirectsUpdatingTargetFileNameAndSizeAndProgress() {
        var expectedResult = "abcd";
        var expectedFileName = "someFile.exe";
        var accessToken = "someAccessToken";
        var targetFileName = new AtomicReference<String>();
        var progress = new DownloadProgress();
        var progressHistory = new ArrayList<ProgressInfo>();
        var outputStream = new ByteArrayOutputStream();
        progress.subscribeToProgress(progressHistory::add);

        when(authService.getAccessToken())
                .thenReturn(accessToken);

        wireMockEmbed.stubFor(get(urlPathEqualTo("/someUrl1"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + accessToken))
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
                .getFileBuffer("/someUrl1", targetFileName, progress);
        DataBufferUtils.write(dataBufferFlux, progress.getTrackedOutputStream(outputStream)).blockFirst();

        String result = outputStream.toString();

        assertEquals(expectedResult, result);
        assertEquals(expectedFileName, targetFileName.get());
        assertEquals(4, progress.getContentLengthBytes());
        assertEquals(1, progressHistory.size());
        assertEquals(100, progressHistory.get(0).percentage());
    }

    @Test
    void getFileBufferShouldThrowIfRequestFails() {
        var accessToken = "someAccessToken";
        var targetFileName = new AtomicReference<String>();
        var progress = new DownloadProgress();

        when(authService.getAccessToken())
                .thenReturn(accessToken);

        wireMockEmbed.stubFor(get(urlPathEqualTo("/someUrl1"))
                .withHeader(GogEmbedWebClient.HEADER_AUTHORIZATION, equalTo("Bearer " + accessToken))
                .willReturn(aResponse()
                        .withStatus(500)));

        @SuppressWarnings("ReactiveStreamsUnusedPublisher")
        Flux<DataBuffer> dataBufferFlux = gogEmbedClient
                .getFileBuffer("/someUrl1", targetFileName, progress);

        assertThrows(GameDownloadRequestFailedException.class, dataBufferFlux::blockFirst);
    }
}