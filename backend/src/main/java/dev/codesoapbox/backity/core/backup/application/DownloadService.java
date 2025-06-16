package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.application.exceptions.ConcurrentFileDownloadException;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileDownloadFailedException;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileDownloadWasCanceledException;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import io.netty.handler.timeout.TimeoutException;
import io.netty.resolver.dns.DnsNameResolverTimeoutException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public class DownloadService {

    private final ConcurrentHashMap<String, ActiveDownload> activeDownloadsByFilePath =
            new ConcurrentHashMap<>();
    private final int maxRetryAttempts;
    private final Duration retryBackoff;

    public void downloadFile(StorageSolution storageSolution, TrackableFileStream trackableFileStream,
                             GameFile gameFile, String filePath)
            throws IOException {
        initializeCancellationTracker(filePath);
        try {
            DownloadProgress progress = trackableFileStream.progress();
            writeToDisk(storageSolution, trackableFileStream.dataStream(), filePath, progress);

            log.info("Downloaded file {} to {}", gameFile, filePath);
            validateDownloadedFileSize(storageSolution, filePath, progress.getContentLengthBytes());
        } finally {
            activeDownloadsByFilePath.remove(filePath);
        }
    }

    private void initializeCancellationTracker(String filePath) {
        ActiveDownload existingActiveDownload =
                activeDownloadsByFilePath.putIfAbsent(filePath, new ActiveDownload());
        if (existingActiveDownload != null) {
            throw new ConcurrentFileDownloadException(filePath);
        }
    }

    private void writeToDisk(StorageSolution storageSolution, Flux<DataBuffer> dataBufferFlux, String filePath,
                             DownloadProgress progress)
            throws IOException {
        try (OutputStream outputStream = storageSolution.getOutputStream(filePath)) {
            DataBufferUtils
                    .write(dataBufferFlux, progress.track(outputStream))
                    .retryWhen(Retry.backoff(maxRetryAttempts, retryBackoff)
                            .filter(this::isRecoverable))
                    .takeUntilOther(activeDownloadsByFilePath.get(filePath).getCancelSignal().asFlux())
                    .doOnNext(DataBufferUtils::release)
                    .blockLast();
            if (shouldCancelDownload(filePath)) {
                throw new FileDownloadWasCanceledException(filePath);
            }
        } catch (FileDownloadWasCanceledException | ConcurrentFileDownloadException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new FileDownloadFailedException("Failed to download file '" + filePath + "'", e);
        }
    }

    private boolean isRecoverable(Throwable throwable) {
        return throwable instanceof DnsNameResolverTimeoutException
               || throwable instanceof WebClientRequestException
               || throwable instanceof reactor.netty.http.client.PrematureCloseException
               || throwable instanceof ConnectException
               || throwable instanceof TimeoutException
               || (throwable instanceof WebClientResponseException resp
                   && resp.getStatusCode().is5xxServerError());
    }

    private boolean shouldCancelDownload(String filePath) {
        ActiveDownload activeDownload = activeDownloadsByFilePath.get(filePath);
        return activeDownload.shouldCancel.get();
    }

    private void validateDownloadedFileSize(
            StorageSolution storageSolution, String filePath, long expectedSizeInBytes) {
        long sizeInBytesOnDisk = storageSolution.getSizeInBytes(filePath);
        if (sizeInBytesOnDisk != expectedSizeInBytes) {
            throw new FileDownloadFailedException(
                    "The downloaded size of " + filePath + " is not what was expected (was "
                    + sizeInBytesOnDisk + ", expected " + expectedSizeInBytes + ")");
        } else {
            log.info("Filesize check for {} passed successfully", filePath);
        }
    }

    public void cancelDownload(@NonNull String filePath) {
        ActiveDownload activeDownload = activeDownloadsByFilePath.get(filePath);
        if (activeDownload != null) {
            activeDownload.triggerCancellation();
        }
    }

    @Getter
    private static class ActiveDownload {

        private final AtomicBoolean shouldCancel = new AtomicBoolean(false);
        private final Sinks.Many<Boolean> cancelSignal = Sinks.many().replay().latest();

        public void triggerCancellation() {
            shouldCancel.set(true);
            cancelSignal.tryEmitNext(true);
        }
    }
}
