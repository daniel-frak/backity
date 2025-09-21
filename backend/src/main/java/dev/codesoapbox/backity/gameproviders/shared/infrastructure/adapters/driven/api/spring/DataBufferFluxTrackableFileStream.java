package dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring;

import dev.codesoapbox.backity.DoNotMutate;
import dev.codesoapbox.backity.core.backup.application.TrackableFileStream;
import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.backup.application.exceptions.ConcurrentFileWriteException;
import dev.codesoapbox.backity.core.backup.application.exceptions.StorageSolutionWriteFailedException;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileWriteWasCanceledException;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import io.netty.handler.timeout.TimeoutException;
import io.netty.resolver.dns.DnsNameResolverTimeoutException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpRetryException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Set;

@Slf4j
public record DataBufferFluxTrackableFileStream(
        Flux<DataBuffer> dataBufferFlux,
        OutputStreamProgressTracker outputStreamProgressTracker,
        int maxRetryAttempts,
        Duration retryBackoff
) implements TrackableFileStream {

    private static final Set<Class<? extends Throwable>> ALWAYS_RECOVERABLE_EXCEPTIONS = Set.of(
            DnsNameResolverTimeoutException.class,
            WebClientRequestException.class,
            reactor.netty.http.client.PrematureCloseException.class,
            ConnectException.class,
            TimeoutException.class,
            UnknownHostException.class,
            SSLException.class,
            HttpRetryException.class
    );

    @Override
    public void writeToStorageSolution(StorageSolution storageSolution, String filePath, Flux<Boolean> cancelTrigger) {
        try {
            tryToWriteToDisk(storageSolution, filePath, cancelTrigger);
        } catch (FileWriteWasCanceledException | ConcurrentFileWriteException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new StorageSolutionWriteFailedException(String.format(
                    "Failed to write file '%s' to %s", filePath, storageSolution.getId()), e);
        }
    }

    private void tryToWriteToDisk(StorageSolution storageSolution, String filePath, Flux<Boolean> cancelTrigger) {
        Mono<Boolean> writeWasCancelledMono = Mono.firstWithSignal(
                cancelTrigger.next().thenReturn(true),
                Flux.using(() -> deleteExistingThenGetOutputStream(storageSolution, filePath),
                                os -> DataBufferUtils.write(
                                        dataBufferFlux, outputStreamProgressTracker.track(os)),
                                this::closeQuietly)
                        .retryWhen(failureIsRecoverable(filePath))
                        .takeUntilOther(cancelTrigger)
                        .doOnNext(DataBufferUtils::release)
                        .then(Mono.just(false))
        );

        boolean writeWasCancelled = Boolean.TRUE.equals(writeWasCancelledMono.block());
        if (writeWasCancelled) {
            throw new FileWriteWasCanceledException(filePath, storageSolution);
        }
    }

    @DoNotMutate // Logging is not tested so `.doBeforeRetry` fails mutation
    private RetryBackoffSpec failureIsRecoverable(String filePath) {
        return Retry.backoff(maxRetryAttempts, retryBackoff)
                .filter(this::isRecoverable)
                .doBeforeRetry(retrySignal -> logRetryAttempt(filePath, retrySignal));
    }

    private boolean isRecoverable(Throwable throwable) {
        return ALWAYS_RECOVERABLE_EXCEPTIONS.stream()
                .anyMatch(type -> type.isInstance(throwable))
                || is5xxWebClientResponseException(throwable);
    }

    @DoNotMutate // False positive on instanceof
    private boolean is5xxWebClientResponseException(Throwable throwable) {
        return throwable instanceof WebClientResponseException resp
                && resp.getStatusCode().is5xxServerError();
    }

    @DoNotMutate // Logging is not tested so `.doBeforeRetry` fails mutation
    private void logRetryAttempt(String filePath, Retry.RetrySignal retrySignal) {
        log.warn("Retrying write of {} [attempt {}/{}] after error: {}",
                filePath,
                retrySignal.totalRetries() + 1,
                maxRetryAttempts,
                retrySignal.failure().getClass() + "(" + retrySignal.failure().getMessage() + ")"
        );
    }

    private OutputStream deleteExistingThenGetOutputStream(StorageSolution storageSolution, String filePath)
            throws IOException {
        storageSolution.deleteIfExists(filePath);
        return storageSolution.getOutputStream(filePath);
    }

    @SneakyThrows
    private void closeQuietly(OutputStream os) {
        os.close();
    }
}
