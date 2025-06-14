package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileDownloadWasCancelledException;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.backup.application.exceptions.FileDownloadException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public class DownloadService {

    private final ConcurrentHashMap<String, AtomicBoolean> cancelFlags = new ConcurrentHashMap<>();

    public void downloadFile(StorageSolution storageSolution, TrackableFileStream trackableFileStream,
                             GameFile gameFile, String filePath)
            throws IOException {
        initializeCancelFlag(filePath);
        AtomicBoolean cancelFlag = cancelFlags.get(filePath);
        try {
            DownloadProgress progress = trackableFileStream.progress();
            writeToDisk(storageSolution, trackableFileStream.dataStream(), filePath, progress);

            log.info("Downloaded file {} to {}", gameFile, filePath);

            if(!cancelFlag.get()) {
                // Only validate size if download wasn't canceled
                validateDownloadedFileSize(storageSolution, filePath, progress.getContentLengthBytes());
            } else {
                throw new FileDownloadWasCancelledException(filePath);
            }
        } finally {
            cancelFlags.remove(filePath);
        }
    }

    private void initializeCancelFlag(String filePath) {
        AtomicBoolean existingCancelFlag = cancelFlags.putIfAbsent(filePath, new AtomicBoolean(false));
        if (existingCancelFlag != null) {
            throw new FileDownloadException("File '" + filePath + "' is currently being downloaded by another thread");
        }
    }

    private void writeToDisk(StorageSolution storageSolution, Flux<DataBuffer> dataBufferFlux, String filePath,
                             DownloadProgress progress)
            throws IOException {
        try (OutputStream outputStream = storageSolution.getOutputStream(filePath)) {
            DataBufferUtils
                    .write(dataBufferFlux, progress.track(outputStream))
                    .takeUntil(dataBuffer -> shouldCancelDownload(filePath))
                    .map(DataBufferUtils::release)
                    .blockLast();
        }
    }

    private boolean shouldCancelDownload(String filePath) {
        AtomicBoolean cancelFlag = cancelFlags.get(filePath);
        return cancelFlag.get();
    }

    private void validateDownloadedFileSize(
            StorageSolution storageSolution, String filePath, long expectedSizeInBytes) {
        long sizeInBytesOnDisk = storageSolution.getSizeInBytes(filePath);
        if (sizeInBytesOnDisk != expectedSizeInBytes) {
            throw new FileDownloadException("The downloaded size of " + filePath + " is not what was expected (was "
                                            + sizeInBytesOnDisk + ", expected " + expectedSizeInBytes + ")");
        } else {
            log.info("Filesize check for {} passed successfully", filePath);
        }
    }

    public void cancelDownload(@NonNull String filePath) {
        AtomicBoolean cancelFlag = cancelFlags.get(filePath);
        if (cancelFlag != null) {
            cancelFlag.set(true);
        }
    }
}
