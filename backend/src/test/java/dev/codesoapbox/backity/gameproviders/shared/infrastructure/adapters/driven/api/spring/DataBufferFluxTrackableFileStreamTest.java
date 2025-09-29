package dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring;

import dev.codesoapbox.backity.core.backup.application.WriteDestination;
import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.shared.application.progress.ProgressInfo;
import dev.codesoapbox.backity.core.backup.application.exceptions.ConcurrentFileWriteException;
import dev.codesoapbox.backity.core.backup.application.exceptions.StorageSolutionWriteFailedException;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileWriteWasCanceledException;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.testing.TestDataBufferFlux;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.testing.TestFlux;
import dev.codesoapbox.backity.testing.time.FakeClock;
import io.netty.handler.codec.dns.DefaultDnsQuestion;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.resolver.dns.DnsNameResolverTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.PrematureCloseException;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpRetryException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataBufferFluxTrackableFileStreamTest {

    private static final String A_FILE_PATH = "test/file.txt";
    private static final int NEVER_RETRY = 0;
    private static final Duration NO_RETRY_BACKOFF = Duration.ofSeconds(0);
    private static final Flux<Boolean> NEVER_CANCEL = Flux.never();

    private Clock clock;
    private FakeUnixStorageSolution storage;

    @BeforeEach
    void setUp() {
        clock = FakeClock.atEpochUtc();
        storage = new FakeUnixStorageSolution();
    }

    @SuppressWarnings("SameParameterValue")
    private OutputStreamProgressTracker initializedOutputStreamProgressTrackerFor(String data) {
        var progressTracker = new OutputStreamProgressTracker();
        progressTracker.initializeTracking(data.getBytes().length, clock);
        return progressTracker;
    }

    private void assertThatFileWasSuccessfullyWrittenToDisk(TestFlux<DataBuffer> dataBufferFlux) {
        assertThat(storage.fileExists(A_FILE_PATH)).isTrue();
        assertThat(storage.getFileContent(A_FILE_PATH)).isEqualTo(dataBufferFlux.data());
    }

    static class RecoverableExceptions {

        /**
         * DNS resolution failures that are typically temporary and resolve themselves when DNS servers recover.
         */
        private static final List<Throwable> DNS_EXCEPTIONS = List.of(
                new DnsNameResolverTimeoutException(
                        InetSocketAddress.createUnresolved("test.com", 80),
                        new DefaultDnsQuestion("test.com", DnsRecordType.A),
                        null
                ),
                new java.net.UnknownHostException("unresolved.host.test")
        );

        /**
         * Connection issues that commonly occur due to network instability
         * or temporary server unavailability.
         */
        private static final List<Throwable> CONNECTION_EXCEPTIONS = List.of(
                new ConnectException("Connection refused"),
                PrematureCloseException.TEST_EXCEPTION,
                new WebClientRequestException(
                        new IOException("simulated glitch"),
                        HttpMethod.GET,
                        URI.create("http://test.com/file"),
                        HttpHeaders.EMPTY
                )
        );

        /**
         * Timeout exceptions that occur when the server is slow to respond but may succeed on retry.
         */
        private static final List<Throwable> TIMEOUT_EXCEPTIONS = List.of(
                ReadTimeoutException.INSTANCE
        );

        /**
         * SSL/TLS related issues that may be temporary.
         */
        private static final List<Throwable> SSL_EXCEPTIONS = List.of(
                new SSLException("SSL error")
        );

        /**
         * HTTP specific errors that indicate server-side issues that may resolve with a retry.
         */
        private static final List<Throwable> HTTP_EXCEPTIONS = List.of(
                new HttpRetryException("Retry", 1),
                WebClientResponseException.create(
                        500, "Server error", HttpHeaders.EMPTY, null, null)
        );

        public static final List<Throwable> ALL = Stream.of(
                        DNS_EXCEPTIONS,
                        CONNECTION_EXCEPTIONS,
                        TIMEOUT_EXCEPTIONS,
                        SSL_EXCEPTIONS,
                        HTTP_EXCEPTIONS
                ).flatMap(List::stream)
                .toList();

        public static Throwable anyException() {
            return ALL.getFirst();
        }
    }

    @Nested
    class WriteToStorageSolution {

        @Nested
        class Succeeding {

            @Test
            void shouldDeleteExistingFileBeforeWritingToDisk() {
                TestFlux<DataBuffer> dataBufferFlux = TestDataBufferFlux.succeeding();
                OutputStreamProgressTracker progress = initializedOutputStreamProgressTrackerFor(dataBufferFlux.data());
                var fileStream = new DataBufferFluxTrackableFileStream(
                        dataBufferFlux, progress, NEVER_RETRY, NO_RETRY_BACKOFF);
                storage.createFile(A_FILE_PATH);

                fileStream.writeToStorageSolution(storage, A_FILE_PATH, NEVER_CANCEL);

                assertThat(storage.getFileContent(A_FILE_PATH)).isEqualTo(dataBufferFlux.data());
            }

            @Test
            void shouldWriteDataToDisk() {
                TestFlux<DataBuffer> dataBufferFlux = TestDataBufferFlux.succeeding();
                OutputStreamProgressTracker progress = initializedOutputStreamProgressTrackerFor(dataBufferFlux.data());
                var fileStream = new DataBufferFluxTrackableFileStream(
                        dataBufferFlux, progress, NEVER_RETRY, NO_RETRY_BACKOFF);

                fileStream.writeToStorageSolution(storage, A_FILE_PATH, NEVER_CANCEL);

                assertThatFileWasSuccessfullyWrittenToDisk(dataBufferFlux);
                assertThat(storage.allOutputStreamsWereClosed()).isTrue();
            }

            @Test
            void shouldUpdateOutputStreamProgressDuringWrite() {
                TestFlux<DataBuffer> dataBufferFlux = TestDataBufferFlux.succeeding();
                OutputStreamProgressTracker progressTracker =
                        initializedOutputStreamProgressTrackerFor(dataBufferFlux.data());
                List<ProgressInfo> history = captureProgressHistory(progressTracker);
                var fileStream = new DataBufferFluxTrackableFileStream(
                        dataBufferFlux, progressTracker, NEVER_RETRY, NO_RETRY_BACKOFF);

                fileStream.writeToStorageSolution(storage, A_FILE_PATH, NEVER_CANCEL);

                assertThat(progressTracker.getContentLengthBytes()).isEqualTo(dataBufferFlux.data().length());
                assertThat(progressTracker.getWrittenBytesLength()).isEqualTo(dataBufferFlux.data().length());
                assertThatLastProgressNotificationIsOneHundredPercent(history);
                assertThatHistoricalPercentagesAreIncreasing(history);
            }

            private void assertThatLastProgressNotificationIsOneHundredPercent(List<ProgressInfo> history) {
                assertThat(history).isNotEmpty();
                assertThat(history.getLast().percentage()).isEqualTo(100);
            }

            private void assertThatHistoricalPercentagesAreIncreasing(List<ProgressInfo> history) {
                List<Integer> percentages = history.stream()
                        .map(ProgressInfo::percentage)
                        .toList();
                assertThat(percentages).isSorted();
            }

            private List<ProgressInfo> captureProgressHistory(OutputStreamProgressTracker progressTracker) {
                List<ProgressInfo> history = new ArrayList<>();
                progressTracker.subscribeToProgress(history::add);
                return history;
            }
        }

        @Nested
        class Canceling {

            private static Stream<Arguments> cancelScenarios() {
                final Flux<Boolean> cancelImmediately = Flux.just(true);
                final Flux<Boolean> cancelMidWrite = Flux.just(false)
                        .thenMany(Flux.just(true));
                return Stream.of(
                        Arguments.of("immediately", cancelImmediately),
                        Arguments.of("mid-write", cancelMidWrite)
                );
            }

            @ParameterizedTest(name = "should throw when canceled {0}")
            @MethodSource("cancelScenarios")
            void shouldThrowWhenCanceled(String scenarioName, Flux<Boolean> cancelFlux) {
                TestFlux<DataBuffer> dataBufferFlux = TestDataBufferFlux.succeeding();
                OutputStreamProgressTracker progress = initializedOutputStreamProgressTrackerFor(dataBufferFlux.data());
                var fileStream =
                        new DataBufferFluxTrackableFileStream(dataBufferFlux, progress, NEVER_RETRY, NO_RETRY_BACKOFF);

                assertThatThrownBy(() -> fileStream.writeToStorageSolution(storage, A_FILE_PATH, cancelFlux))
                        .isInstanceOf(FileWriteWasCanceledException.class)
                        .hasMessageContaining(A_FILE_PATH);
                assertThat(storage.allOutputStreamsWereClosed()).isTrue();
            }
        }

        @Nested
        class Retrying {

            private static Stream<Arguments> recoverableRetryFactories() {
                return RecoverableExceptions.ALL.stream().flatMap(exception -> Stream.of(
                        Arguments.of("throws " + exception.getClass().getSimpleName() + " at first chunk",
                                (Supplier<TestFlux<DataBuffer>>) () -> TestDataBufferFlux
                                        .immediatelyFailingThenSucceeding(List.of(exception))),
                        Arguments.of("throws " + exception.getClass().getSimpleName() + " halfway through",
                                (Supplier<TestFlux<DataBuffer>>) () -> TestDataBufferFlux
                                        .failingHalfwayThroughThenSucceeding(List.of(exception)))
                ));
            }

            @ParameterizedTest(name = "should retry and then succeed given {0}")
            @MethodSource("recoverableRetryFactories")
            void shouldRetryAndThenSucceedGivenRecoverableExceptions(
                    String label, Supplier<TestFlux<DataBuffer>> dataBufferFluxFactory) {
                TestFlux<DataBuffer> dataBufferFlux = dataBufferFluxFactory.get();
                OutputStreamProgressTracker progress = initializedOutputStreamProgressTrackerFor(dataBufferFlux.data());
                var fileStream = new DataBufferFluxTrackableFileStream(
                        dataBufferFlux, progress, RecoverableExceptions.ALL.size(), NO_RETRY_BACKOFF);

                fileStream.writeToStorageSolution(storage, A_FILE_PATH, NEVER_CANCEL);

                assertThat(dataBufferFlux.writeAttempts()).isEqualTo(2);
                assertThatFileWasSuccessfullyWrittenToDisk(dataBufferFlux);
                assertThat(storage.allOutputStreamsWereClosed()).isTrue();
            }

            @Test
            void shouldNotRetryGivenFailedWithNon500ClientResponseException() {
                TestFlux<DataBuffer> dataBufferFlux =
                        TestDataBufferFlux.immediatelyFailing(a404WebClientResponseException());
                OutputStreamProgressTracker progress = initializedOutputStreamProgressTrackerFor(dataBufferFlux.data());
                var fileStream = new DataBufferFluxTrackableFileStream(
                        dataBufferFlux, progress, NEVER_RETRY, NO_RETRY_BACKOFF);

                assertThatThrownBy(() -> fileStream.writeToStorageSolution(storage, A_FILE_PATH, NEVER_CANCEL))
                        .isInstanceOf(StorageSolutionWriteFailedException.class);
                assertThat(dataBufferFlux.writeAttempts()).isOne();
                assertThat(storage.allOutputStreamsWereClosed()).isTrue();
            }

            private WebClientResponseException a404WebClientResponseException() {
                return WebClientResponseException.create(
                        404, "Not found", HttpHeaders.EMPTY, null, null);
            }

            @Test
            void shouldThrowGivenMaxRetryAttemptsExceeded() {
                TestFlux<DataBuffer> dataBufferFlux = TestDataBufferFlux
                        .immediatelyFailing(RecoverableExceptions.anyException());
                OutputStreamProgressTracker progress = initializedOutputStreamProgressTrackerFor(dataBufferFlux.data());
                int maxRetryAttempts = 3;
                var fileStream = new DataBufferFluxTrackableFileStream(
                        dataBufferFlux, progress, maxRetryAttempts, NO_RETRY_BACKOFF);

                assertThatThrownBy(() -> fileStream.writeToStorageSolution(storage, A_FILE_PATH, NEVER_CANCEL))
                        .isInstanceOf(StorageSolutionWriteFailedException.class);
                assertThat(dataBufferFlux.writeAttempts())
                        .isEqualTo(maxRetryAttempts + 1);
            }
        }

        @Nested
        class ConcurrentFileWrites {

            @Test
            void shouldPropagateConcurrentFileWriteException() {
                TestFlux<DataBuffer> dataBufferFlux = TestDataBufferFlux.succeeding();
                OutputStreamProgressTracker progress = initializedOutputStreamProgressTrackerFor(dataBufferFlux.data());
                var fileStream = new DataBufferFluxTrackableFileStream(
                        dataBufferFlux, progress, NEVER_RETRY, NO_RETRY_BACKOFF);
                var writeDestination = new WriteDestination(storage.getId(), A_FILE_PATH);
                storage.setShouldThrowOnFileDeletion(new ConcurrentFileWriteException(writeDestination));

                assertThatThrownBy(() -> fileStream.writeToStorageSolution(storage, A_FILE_PATH, NEVER_CANCEL))
                        .isInstanceOf(ConcurrentFileWriteException.class);
                assertThat(storage.allOutputStreamsWereClosed()).isTrue();
            }
        }

        @Nested
        class WrappingExceptions {

            private static Stream<Arguments> wrappingScenarios() {

                return Stream.of(
                        Arguments.of(
                                "retries exceeded for recoverable exception thrown during write",
                                TestDataBufferFlux.immediatelyFailing(RecoverableExceptions.anyException()),
                                (Consumer<FakeUnixStorageSolution>) _ -> {
                                    // Do nothing
                                }
                        ),
                        Arguments.of(
                                "non-recoverable exception thrown during write",
                                TestDataBufferFlux.immediatelyFailing(new IllegalStateException("Test exception")),
                                (Consumer<FakeUnixStorageSolution>) _ -> {
                                    // Do nothing
                                }
                        ),
                        Arguments.of(
                                "file deletion throws exception",
                                TestDataBufferFlux.immediatelyFailing(new IllegalStateException("Test exception")),
                                (Consumer<FakeUnixStorageSolution>) storage -> {
                                    var testException = new RuntimeException("Test exception");
                                    storage.setShouldThrowOnFileDeletion(testException);
                                }
                        ),
                        Arguments.of(
                                "OutputStream retrieval throws exception",
                                TestDataBufferFlux.immediatelyFailing(new IllegalStateException("Test exception")),
                                (Consumer<FakeUnixStorageSolution>) storage -> {
                                    var testException = new RuntimeException("Test exception");
                                    storage.setShouldThrowOnGetOutputStream(testException);
                                }
                        )
                );
            }

            @ParameterizedTest(name = "should wrap exception given {0}")
            @MethodSource("wrappingScenarios")
            void shouldWrapExceptions(
                    String label, TestFlux<DataBuffer> dataBufferFlux, Consumer<FakeUnixStorageSolution> storageSetup) {
                storageSetup.accept(storage);
                OutputStreamProgressTracker progress = initializedOutputStreamProgressTrackerFor(dataBufferFlux.data());
                var fileStream = new DataBufferFluxTrackableFileStream(
                        dataBufferFlux, progress, NEVER_RETRY, NO_RETRY_BACKOFF);

                assertThatThrownBy(() -> fileStream.writeToStorageSolution(storage, A_FILE_PATH, NEVER_CANCEL))
                        .isInstanceOf(StorageSolutionWriteFailedException.class)
                        .hasMessage("Failed to write file '" + A_FILE_PATH + "' to " + storage.getId());
                assertThat(storage.allOutputStreamsWereClosed()).isTrue();
            }
        }
    }
}
