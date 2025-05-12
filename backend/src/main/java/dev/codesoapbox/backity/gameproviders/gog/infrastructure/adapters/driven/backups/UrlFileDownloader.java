package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.backups;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.BackupProgress;
import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.gameproviders.gog.application.FileBufferProvider;
import dev.codesoapbox.backity.gameproviders.gog.domain.exceptions.FileBackupException;
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

    public void downloadFile(FileBufferProvider fileBufferProvider, GameFile gameFile, String filePath,
                             BackupProgress progress) throws IOException {
        String url = gameFile.getGameProviderFile().url();
        Flux<DataBuffer> dataBufferFlux = fileBufferProvider.getFileBuffer(url, progress);
        writeToDisk(dataBufferFlux, filePath, progress);

        log.info("Downloaded file {} to {}", gameFile, filePath);

        validateDownloadedFileSize(filePath, progress.getContentLengthBytes());
    }

    private void writeToDisk(Flux<DataBuffer> dataBufferFlux, String filePath, BackupProgress progress)
            throws IOException {
        try (OutputStream outputStream = fileManager.getOutputStream(filePath)) {
            DataBufferUtils
                    .write(dataBufferFlux, progress.track(outputStream))
                    .map(DataBufferUtils::release)
                    .blockLast();
        }
    }

    private void validateDownloadedFileSize(String filePath, long expectedSizeInBytes) {
        long sizeInBytesOnDisk = fileManager.getSizeInBytes(filePath);
        if (sizeInBytesOnDisk != expectedSizeInBytes) {
            throw new FileBackupException("The downloaded size of " + filePath + " is not what was expected (was "
                                          + sizeInBytesOnDisk + ", expected " + expectedSizeInBytes + ")");
        } else {
            log.info("Filesize check for {} passed successfully", filePath);
        }
    }
}
