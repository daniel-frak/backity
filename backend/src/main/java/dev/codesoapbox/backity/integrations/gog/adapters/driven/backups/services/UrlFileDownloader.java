package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.backup.domain.BackupProgress;
import dev.codesoapbox.backity.core.backup.domain.BackupProgressFactory;
import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.FileBackupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@RequiredArgsConstructor
public class UrlFileDownloader {

    private final FileManager fileManager;

    public String downloadFile(FileBufferProvider fileBufferProvider, GameFile gameFile, String tempFilePath,
                               BackupProgress progress)
            throws IOException {
        String url = gameFile.getGameProviderFile().url();
        Flux<DataBuffer> dataBufferFlux = fileBufferProvider.getFileBuffer(url, progress);
        writeToDisk(dataBufferFlux, tempFilePath, progress);

        log.info("Downloaded file {} to {}", gameFile, tempFilePath);

        validateDownloadedFileSize(tempFilePath, progress.getContentLengthBytes());

        String originalFileName = gameFile.getGameProviderFile().originalFileName();
        return fileManager.renameFileAddingSuffixIfExists(tempFilePath, originalFileName);
    }

    private void writeToDisk(Flux<DataBuffer> dataBufferFlux, String tempFilePath, BackupProgress progress)
            throws IOException {
        try (OutputStream outputStream = fileManager.getOutputStream(tempFilePath)) {
            DataBufferUtils
                    .write(dataBufferFlux, progress.track(outputStream))
                    .map(DataBufferUtils::release)
                    .blockLast();
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
