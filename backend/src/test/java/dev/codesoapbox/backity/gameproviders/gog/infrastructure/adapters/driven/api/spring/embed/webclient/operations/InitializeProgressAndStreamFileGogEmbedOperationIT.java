package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.TestSourceFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.exceptions.GameBackupRequestFailedException;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.config.GogProperties;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.config.GogWebClientConfig;
import dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring.DataBufferFluxTrackableFileStream;
import dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring.DataBufferFluxTrackableFileStreamFactory;
import dev.codesoapbox.backity.shared.application.progress.ProgressInfo;
import dev.codesoapbox.backity.testing.wiremock.CustomWireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitializeProgressAndStreamFileGogEmbedOperationIT {

    @RegisterExtension
    private static final WireMockExtension wireMock = CustomWireMockExtension.newInstance();

    private static final String ACCESS_TOKEN = "someAccessToken";
    private static final String AUTH_HEADER_VALUE = "Bearer " + ACCESS_TOKEN;

    @Mock
    private DataBufferFluxTrackableFileStreamFactory trackableFileStreamFactory;

    @Mock
    private Clock clock;

    @Mock
    private GogAuthService authService;

    private InitializeProgressAndStreamFileGogEmbedOperation operation;

    @BeforeEach
    void setUp() {
        WebClient webClient = buildWebClient();

        operation = new InitializeProgressAndStreamFileGogEmbedOperation(
                webClient, authService, trackableFileStreamFactory, clock);

        authServiceProvidesAccessToken();
        trackableFileStreamFactoryReturnsTrackableFileStream();
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

    @SuppressWarnings("unchecked")
    private void trackableFileStreamFactoryReturnsTrackableFileStream() {
        lenient().when(trackableFileStreamFactory.create(any(Flux.class), any(OutputStreamProgressTracker.class)))
                .thenAnswer(inv -> {
                    DataBufferFluxTrackableFileStream trackableFileStream = mock();
                    lenient().when(trackableFileStream.dataBufferFlux())
                            .thenReturn(inv.getArgument(0, Flux.class));
                    return trackableFileStream;
                });
    }

    private SourceFile aGogFile() {
        return TestSourceFile.gogBuilder()
                .url("/someUrl1")
                .build();
    }

    @Nested
    class Successful {

        @Test
        void shouldStreamFile() {
            var expectedFileContent = "abcd";
            SourceFile gogFile = mockAuthenticatedGogFileRetrievalWithoutRedirects(expectedFileContent);
            OutputStreamProgressTracker progress = new OutputStreamProgressTracker();

            DataBufferFluxTrackableFileStream trackableFileStream = operation
                    .execute(gogFile, progress);
            String fileContent = writeFile(trackableFileStream, progress);

            assertThat(fileContent).isEqualTo(expectedFileContent);
        }

        private String writeFile(DataBufferFluxTrackableFileStream trackableFileStream,
                                 OutputStreamProgressTracker progressTracker) {
            var outputStream = new ByteArrayOutputStream();
            DataBufferUtils.write(
                    trackableFileStream.dataBufferFlux(),
                    progressTracker.track(outputStream)
            ).blockFirst();
            return outputStream.toString();
        }

        private SourceFile mockAuthenticatedGogFileRetrievalWithoutRedirects(
                String expectedFileContent) {
            SourceFile gogFile = aGogFile();
            wireMock.stubFor(get(urlPathEqualTo(gogFile.getUrl()))
                    .withHeader(GogEmbedHeaders.AUTHORIZATION,
                            equalTo(AUTH_HEADER_VALUE))
                    .willReturn(aResponse()
                            .withBody(expectedFileContent.getBytes(StandardCharsets.UTF_8))
                            .withStatus(200)));
            return gogFile;
        }

        @Test
        void shouldInitializeOutputStreamProgress() {
            var expectedFileContent = "abcd";
            SourceFile gogFile = mockAuthenticatedGogFileRetrievalWithoutRedirects(expectedFileContent);
            var progress = new OutputStreamProgressTracker();
            List<ProgressInfo> progressHistory = trackProgressHistory(progress);

            DataBufferFluxTrackableFileStream trackableFileStream = operation
                    .execute(gogFile, progress);
            writeFile(trackableFileStream, progress);

            assertProgressWasTrackedCorrectly(progress, progressHistory, expectedFileContent);
        }

        private List<ProgressInfo> trackProgressHistory(OutputStreamProgressTracker progressTracker) {
            var progressHistory = new ArrayList<ProgressInfo>();
            progressTracker.subscribeToProgress(progressHistory::add);
            return progressHistory;
        }

        private void assertProgressWasTrackedCorrectly(OutputStreamProgressTracker progressTracker,
                                                       List<ProgressInfo> progressHistory,
                                                       String expectedFileContent) {
            assertThat(progressTracker.getContentLengthBytes()).isEqualTo(expectedFileContent.getBytes().length);
            assertThat(progressHistory).hasSize(1);
            assertThat(progressHistory.getFirst().percentage()).isEqualTo(100);
        }

        @Test
        void shouldStreamFileGivenUrlRedirects() {
            var expectedFileContent = "abcd";
            var progress = new OutputStreamProgressTracker();
            SourceFile gogFile = mockAuthenticatedGogFileRetrievalWithRedirects(expectedFileContent);

            DataBufferFluxTrackableFileStream fileStream = operation
                    .execute(gogFile, progress);
            String fileContent = writeFile(fileStream, progress);

            assertThat(fileContent).isEqualTo(expectedFileContent);
        }

        private SourceFile mockAuthenticatedGogFileRetrievalWithRedirects(
                String expectedFileContent) {
            SourceFile gogFile = aGogFile();

            wireMock.stubFor(get(urlPathEqualTo(gogFile.getUrl()))
                    .withHeader(GogEmbedHeaders.AUTHORIZATION,
                            equalTo(AUTH_HEADER_VALUE))
                    .willReturn(aResponse()
                            .withHeader("Location", gogFile.getUrl() + "-2")
                            .withStatus(302)));
            wireMock.stubFor(get(urlPathEqualTo(gogFile.getUrl() + "-2"))
                    .willReturn(aResponse()
                            .withHeader("Location", gogFile.getUrl() + "-3/")
                            .withStatus(302)));
            wireMock.stubFor(get(urlPathEqualTo(gogFile.getUrl() + "-3/"))
                    .willReturn(aResponse()
                            .withBody(expectedFileContent.getBytes(StandardCharsets.UTF_8))
                            .withStatus(200)));

            return gogFile;
        }

        @Test
        void shouldStreamFileGivenUrlRedirectsAndHasQueryParams() {
            var expectedFileContent = "abcd";
            var progress = new OutputStreamProgressTracker();
            SourceFile gogFile = mockAuthenticatedGogFileRetrievalWithRedirectsAndQueryParams(expectedFileContent);

            DataBufferFluxTrackableFileStream fileStream = operation
                    .execute(gogFile, progress);
            String fileContent = writeFile(fileStream, progress);

            assertThat(fileContent).isEqualTo(expectedFileContent);
        }

        private SourceFile mockAuthenticatedGogFileRetrievalWithRedirectsAndQueryParams(String expectedResult) {
            SourceFile gogFile = aGogFile();
            wireMock.stubFor(get(urlPathEqualTo(gogFile.getUrl()))
                    .withHeader(GogEmbedHeaders.AUTHORIZATION,
                            equalTo(AUTH_HEADER_VALUE))
                    .willReturn(aResponse()
                            .withHeader("Location", gogFile.getUrl() + "-2")
                            .withStatus(302)));
            wireMock.stubFor(get(urlPathEqualTo(gogFile.getUrl() + "-2"))
                    .willReturn(aResponse()
                            .withHeader("Location", gogFile.getUrl() + "-3?param=true")
                            .withStatus(302)));
            wireMock.stubFor(get(urlPathEqualTo(gogFile.getUrl() + "-3"))
                    .withQueryParam("param", equalTo("true"))
                    .willReturn(aResponse()
                            .withBody(expectedResult.getBytes(StandardCharsets.UTF_8))
                            .withStatus(200)));

            return gogFile;
        }
    }

    @Nested
    class Throwing {

        @Test
        void shouldThrowIfRequestFails() {
            SourceFile gogFile = aGogFile();
            var progress = new OutputStreamProgressTracker();
            mockGogFileRetrievalFails(gogFile);

            DataBufferFluxTrackableFileStream fileStream = operation
                    .execute(gogFile, progress);

            Flux<DataBuffer> dataBufferFlux = fileStream.dataBufferFlux();
            assertThatThrownBy(dataBufferFlux::blockFirst)
                    .isInstanceOf(GameBackupRequestFailedException.class);
        }

        private void mockGogFileRetrievalFails(SourceFile gogFile) {
            wireMock.stubFor(get(urlPathEqualTo(gogFile.getUrl()))
                    .withHeader(GogEmbedHeaders.AUTHORIZATION,
                            equalTo(AUTH_HEADER_VALUE))
                    .willReturn(aResponse()
                            .withStatus(500)));
        }
    }
}
