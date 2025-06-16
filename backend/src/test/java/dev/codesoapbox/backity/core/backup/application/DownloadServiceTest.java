package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.application.exceptions.ConcurrentFileDownloadException;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileDownloadFailedException;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileDownloadWasCanceledException;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.testing.FakeTrackableFileStreamFactory;
import io.netty.handler.codec.dns.DefaultDnsQuestion;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.TimeoutException;
import io.netty.resolver.dns.DnsNameResolverTimeoutException;
import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.PrematureCloseException;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpRetryException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DownloadServiceTest {

    private static final List<Throwable> RECOVERABLE_EXCEPTIONS =
            List.of(aWebClientRequestException(), aDnsResolverTimeoutException(), aPrematureCloseException(),
                    a500WebClientResponseException(), aConnectException(), aTimeoutException(),
                    anSSLException(), anHttpRetryException());

    private DownloadService downloadService;
    private FakeUnixStorageSolution storageSolution;
    private FakeTrackableFileStreamFactory fileStreamFactory;
    private ThrowingRunnable onFileDownloadStarted;

    @Mock
    private Clock clock;

    private static WebClientRequestException aWebClientRequestException() {
        return new WebClientRequestException(
                new IOException("simulated glitch"),
                HttpMethod.GET,
                URI.create("http://test.com/file"),
                HttpHeaders.EMPTY
        );
    }

    private static DnsNameResolverTimeoutException aDnsResolverTimeoutException() {
        return new DnsNameResolverTimeoutException(
                InetSocketAddress.createUnresolved("test.com", 80),
                new DefaultDnsQuestion("test.com", DnsRecordType.A),
                null
        );
    }

    private static PrematureCloseException aPrematureCloseException() {
        return PrematureCloseException.TEST_EXCEPTION;
    }

    private static WebClientResponseException a500WebClientResponseException() {
        return WebClientResponseException.create(
                500, "Server error", HttpHeaders.EMPTY, null, null);
    }

    private static ConnectException aConnectException() {
        return new ConnectException("Connection refused");
    }

    private static TimeoutException aTimeoutException() {
        return ReadTimeoutException.INSTANCE;
    }

    private static SSLException anSSLException() {
        return new SSLException("SSL error");
    }

    private static HttpRetryException anHttpRetryException() {
        return new HttpRetryException("Retry", 1);
    }

    @BeforeEach
    void setUp() {
        storageSolution = new FakeUnixStorageSolution();
        fileStreamFactory = new FakeTrackableFileStreamFactory(clock);
        downloadService = new DownloadService(RECOVERABLE_EXCEPTIONS.size(), Duration.ofMillis(1));
        onFileDownloadStarted = null;
    }

    private DownloadProgress mockDownloadProgress(long contentLengthBytes) {
        DownloadProgress downloadProgress = mock(DownloadProgress.class);
        lenient().when(downloadProgress.track(any()))
                .thenAnswer(inv -> {
                    if (onFileDownloadStarted != null) {
                        onFileDownloadStarted.run();
                    }
                    return inv.getArgument(0);
                });
        lenient().when(downloadProgress.getContentLengthBytes())
                .thenReturn(contentLengthBytes);

        return downloadProgress;
    }

    @Nested
    class Downloading {

        @Test
        void downloadFileShouldDownloadToDisk() {
            GameFile gameFile = TestGameFile.gog();
            String filePath = "testFilePath";
            String testData = "Test data";
            DownloadProgress downloadProgress = mockDownloadProgress(testData.length());
            TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, testData);

            downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath);

            assertThat(storageSolution.fileExists(filePath)).isTrue();
            assertThat(storageSolution.getSizeInBytes(filePath)).isEqualTo(testData.length());
            assertThat(storageSolution.allOutputStreamsWereClosed()).isTrue();
        }

        @Test
        void downloadFileShouldTrackProgress() {
            String testData = "Test data";
            DownloadProgress downloadProgress = mockDownloadProgress(testData.length());
            GameFile gameFile = TestGameFile.gog();
            TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, testData);

            downloadService.downloadFile(storageSolution, fileStream, gameFile, "someFilePath");

            InOrder inOrder = Mockito.inOrder(downloadProgress);
            inOrder.verify(downloadProgress).initializeTracking(9, clock);
            inOrder.verify(downloadProgress).track(any());
        }

        @Test
        void downloadFileShouldThrowGivenFileSizeDoesNotMatch() {
            String filePath = "someFilePath";
            GameFile gameFile = TestGameFile.gog();
            storageSolution.overrideDownloadedSizeFor(filePath, 999L);
            String testData = "Test data";
            DownloadProgress downloadProgress = mockDownloadProgress(testData.length());
            TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, testData);

            assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                    .isInstanceOf(FileDownloadFailedException.class)
                    .message()
                    .isEqualTo("The downloaded size of someFilePath" +
                               " is not what was expected (was 999, expected 9)");
            assertThat(storageSolution.allOutputStreamsWereClosed()).isTrue();
        }

        @Test
        void downloadFileShouldThrowGivenAlreadyDownloading() {
            GameFile gameFile = TestGameFile.gog();
            String filePath = "testFilePath";
            String testData = "test data";
            DownloadProgress downloadProgress = mockDownloadProgress(testData.length());
            TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, testData);
            onFileDownloadStarted = () -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath);

            assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                    .isInstanceOf(ConcurrentFileDownloadException.class);
            assertThat(storageSolution.allOutputStreamsWereClosed()).isTrue();
        }

        @Test
        void downloadFileShouldThrowFileDownloadFailedExceptionForRecoverableExceptionGivenRetriesExceeded() {
            GameFile gameFile = TestGameFile.gog();
            var filePath = "errorPath";
            var testData = "Test data";
            DownloadProgress downloadProgress = mockDownloadProgress(0L);
            TrackableFileStream fileStream =
                    fileStreamFactory.createFailing(downloadProgress, testData, aWebClientRequestException());

            assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                    .isInstanceOf(FileDownloadFailedException.class)
                    .hasMessage("Failed to download file 'errorPath'");
            assertThat(storageSolution.allOutputStreamsWereClosed()).isTrue();
        }

        @Test
        void downloadFileShouldRetryOnTransientErrorAndThenSucceed() {
            GameFile gameFile = TestGameFile.gog();
            String filePath = "retryPath";
            String testData = "Test data";
            DownloadProgress progress = mockDownloadProgress(testData.length());
            AtomicInteger numOfTries = new AtomicInteger(0);
            TrackableFileStream fileStream =
                    fileStreamFactory.createInitiallyFailing(progress, testData, RECOVERABLE_EXCEPTIONS, numOfTries);

            downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath);

            assertThat(numOfTries.get()).isEqualTo(oneMoreThanRecoverableExceptions());
            assertThat(storageSolution.fileExists(filePath)).isTrue();
            assertThat(storageSolution.getSizeInBytes(filePath)).isEqualTo(testData.length());
            assertThat(storageSolution.allOutputStreamsWereClosed()).isTrue();
        }

        private int oneMoreThanRecoverableExceptions() {
            return RECOVERABLE_EXCEPTIONS.size() + 1;
        }

        @Test
        void downloadFileShouldNotRetryMostErrors() {
            GameFile gameFile = TestGameFile.gog();
            String filePath = "retryPath";
            String testData = "Test data";
            DownloadProgress progress = mockDownloadProgress(testData.length());
            AtomicInteger numOfTries = new AtomicInteger(0);
            var unrecoverableException = new RuntimeException("Unrecoverable exception");
            TrackableFileStream fileStream = fileStreamFactory.createInitiallyFailing(
                    progress, testData, List.of(unrecoverableException), numOfTries);

            assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                    .isInstanceOf(FileDownloadFailedException.class)
                    .hasCause(unrecoverableException);
            assertThat(numOfTries.get()).isEqualTo(1);
            assertThat(storageSolution.fileExists(filePath)).isFalse();
            assertThat(storageSolution.allOutputStreamsWereClosed()).isTrue();
        }

        @Test
        void downloadFileShouldNotRetryWebClientResponseExceptionGivenNot500() {
            GameFile gameFile = TestGameFile.gog();
            String filePath = "retryPath";
            String testData = "Test data";
            DownloadProgress progress = mockDownloadProgress(testData.length());
            AtomicInteger numOfTries = new AtomicInteger(0);
            var unrecoverableException = a404WebClientResponseException();
            TrackableFileStream fileStream = fileStreamFactory.createInitiallyFailing(
                    progress, testData, List.of(unrecoverableException), numOfTries);

            assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                    .isInstanceOf(FileDownloadFailedException.class)
                    .hasCause(unrecoverableException);
            assertThat(numOfTries.get()).isEqualTo(1);
            assertThat(storageSolution.fileExists(filePath)).isFalse();
            assertThat(storageSolution.allOutputStreamsWereClosed()).isTrue();
        }

        private static WebClientResponseException a404WebClientResponseException() {
            return WebClientResponseException.create(
                    404, "Not found", HttpHeaders.EMPTY, null, null);
        }

        @Test
        void downloadFileShouldNotCorruptFileGivenTransientErrorInTheMiddleOfDownload() {
            GameFile gameFile = TestGameFile.gog();
            String filePath = "testFilePath";
            String testData = "Test data";
            DownloadProgress downloadProgress = mockDownloadProgress(testData.length());
            TrackableFileStream fileStream = fileStreamFactory.createFailingHalfwayThrough(downloadProgress, testData,
                    RECOVERABLE_EXCEPTIONS.getFirst());

            downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath);

            assertThat(storageSolution.fileExists(filePath)).isTrue();
            assertThat(storageSolution.getSizeInBytes(filePath)).isEqualTo(testData.length());
            assertThat(storageSolution.allOutputStreamsWereClosed()).isTrue();
        }
    }

    @Nested
    class Canceling {

        @Test
        void shouldCancelDownload() {
            GameFile gameFile = TestGameFile.gog();
            String filePath = "testFilePath";
            String testData = "Test data";
            DownloadProgress downloadProgress = mockDownloadProgress(testData.length());
            TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, testData);

            onFileDownloadStarted = () -> downloadService.cancelDownload(filePath);

            assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                    .isInstanceOf(FileDownloadWasCanceledException.class);
            assertThat(storageSolution.fileExists(filePath)).isFalse();
            assertThat(storageSolution.allOutputStreamsWereClosed()).isTrue();
        }

        @Test
        void shouldNotValidateSizeGivenDownloadWasCanceled() {
            GameFile gameFile = TestGameFile.gog();
            String filePath = "testFilePath";
            String testData = "Test data";
            DownloadProgress downloadProgress = mockDownloadProgress(testData.length());
            TrackableFileStream fileStream = fileStreamFactory.create(downloadProgress, testData);
            storageSolution.setCustomWrittenSizeInBytes(testData.length() + 1L); // Bigger downloaded than expected size
            onFileDownloadStarted = () -> downloadService.cancelDownload(filePath);

            assertThatThrownBy(() -> downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath))
                    .isInstanceOf(FileDownloadWasCanceledException.class);
            assertThat(storageSolution.allOutputStreamsWereClosed()).isTrue();
        }

        @Test
        void cancelDownloadShouldNotThrowGivenFileIsNotCurrentlyBeingDownloaded() {
            assertThatCode(() -> downloadService.cancelDownload("nonExistentFilePath"))
                    .doesNotThrowAnyException();
            assertThat(storageSolution.allOutputStreamsWereClosed()).isTrue();
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void cancelDownloadShouldThrowGivenNullFilePath() {
            assertThatThrownBy(() -> downloadService.cancelDownload(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("filePath");
            assertThat(storageSolution.allOutputStreamsWereClosed()).isTrue();
        }
    }
}