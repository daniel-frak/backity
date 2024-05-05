package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.backup.domain.BackupProgress;
import dev.codesoapbox.backity.core.discovery.domain.ProgressInfo;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.FileBackupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class UrlFileDownloader {

    private final FileManager fileManager;
    private final Consumer<ProgressInfo> progressInfoConsumer;
    private final Supplier<BackupProgress> backupProgressFactory;

    public String downloadFile(FileBufferProvider fileBufferProvider, FileDetails fileDetails,
                               String tempFilePath)
            throws IOException {
        BackupProgress progress = backupProgressFactory.get();
        String url = fileDetails.getSourceFileDetails().url();
        Flux<DataBuffer> dataBufferFlux = fileBufferProvider.getFileBuffer(url, progress);
        writeToDisk(dataBufferFlux, tempFilePath, progress);

        log.info("Downloaded file {} to {}", fileDetails, tempFilePath);

        validateDownloadedFileSize(tempFilePath, progress.getContentLengthBytes());

        String originalFileName = fileDetails.getSourceFileDetails().originalFileName();
        return fileManager.renameFileAddingSuffixIfExists(tempFilePath, originalFileName);
    }

    private void writeToDisk(Flux<DataBuffer> dataBufferFlux, String tempFilePath, BackupProgress progress)
            throws IOException {
        try (OutputStream outputStream = fileManager.getOutputStream(tempFilePath)) {
            progress.subscribeToProgress(progressInfoConsumer);

            DataBufferUtils
                    .write(dataBufferFlux, progress.getTrackedOutputStream(outputStream))
                    .map(DataBufferUtils::release)
                    .blockLast();
        } finally {
            progress.unsubscribeFromProgress(progressInfoConsumer);
        }
    }

    private void validateDownloadedFileSize(String tempFilePath, long expectedSizeInBytes) {
        long sizeInBytesOnDisk = fileManager.getSizeInBytes(tempFilePath);
        if (sizeInBytesOnDisk != expectedSizeInBytes) {
            throw new FileBackupException("The downloaded size of " + tempFilePath + " is not what was expected (was "
                    + sizeInBytesOnDisk + ", expected " + expectedSizeInBytes + ")");
        } else {
            log.info("Filesize check for {} passed successfully", tempFilePath);
        }
    }
}
