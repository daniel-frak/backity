package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import dev.codesoapbox.backity.core.files.domain.backup.services.BackupProgress;
import dev.codesoapbox.backity.core.files.domain.backup.services.FileManager;
import dev.codesoapbox.backity.core.files.domain.discovery.model.ProgressInfo;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.FileBackupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class UrlFileDownloader {

    private final FileManager fileManager;
    private final Consumer<ProgressInfo> progressInfoConsumer;

    public String downloadGameFile(FileBufferProvider fileBufferProvider, GameFileVersionBackup gameFileVersionBackup,
                                   String tempFilePath) throws IOException {
        var progress = new BackupProgress();
        Flux<DataBuffer> dataBufferFlux = fileBufferProvider.getFileBuffer(gameFileVersionBackup.getUrl(), progress);
        writeToDisk(dataBufferFlux, tempFilePath, progress);

        log.info("Downloaded file {} to {}", gameFileVersionBackup, tempFilePath);

        validateDownloadedFileSize(tempFilePath, progress.getContentLengthBytes());

        // @TODO Write test for return value
        return fileManager.renameFileAddingSuffixIfExists(tempFilePath, gameFileVersionBackup.getOriginalFileName());
    }

    private void writeToDisk(Flux<DataBuffer> dataBufferFlux, String tempFilePath, BackupProgress progress)
            throws IOException {
        try (OutputStream outputStream = fileManager.getOutputStream(tempFilePath)) {
            progress.subscribeToProgress(progressInfoConsumer);

            DataBufferUtils
                    .write(dataBufferFlux, progress.getTrackedOutputStream(outputStream))
                    .map(DataBufferUtils::release)
                    .blockLast();
        } catch (FileNotFoundException e) {
            throw new FileBackupException("Unable to create file", e);
        } finally {
            progress.unsubscribeFromProgress(progressInfoConsumer);
        }
    }

    private void validateDownloadedFileSize(String tempFilePath, long sizeInBytes) {
        File downloadedFile = new File(tempFilePath);
        if (downloadedFile.length() != sizeInBytes) {
            throw new FileBackupException("The downloaded size of " + tempFilePath + " is not what was expected ("
                    + downloadedFile.length() + " vs " + sizeInBytes + ")");
        } else {
            log.info("Filesize check for {} passed successfully", tempFilePath);
        }
    }
}
