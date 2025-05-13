package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.backups;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.BackupProgress;
import dev.codesoapbox.backity.core.filemanagement.domain.FileSystem;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.gameproviders.gog.application.TrackableFileStream;
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

    private final FileSystem fileSystem;

    public void downloadFile(TrackableFileStream trackableFileStream, GameFile gameFile, String filePath)
            throws IOException {
        BackupProgress progress = trackableFileStream.progress();
        writeToDisk(trackableFileStream.dataStream(), filePath, progress);

        log.info("Downloaded file {} to {}", gameFile, filePath);

        validateDownloadedFileSize(filePath, progress.getContentLengthBytes());
    }

    private void writeToDisk(Flux<DataBuffer> dataBufferFlux, String filePath, BackupProgress progress)
            throws IOException {
        try (OutputStream outputStream = fileSystem.getOutputStream(filePath)) {
            DataBufferUtils
                    .write(dataBufferFlux, progress.track(outputStream))
                    .map(DataBufferUtils::release)
                    .blockLast();
        }
    }

    private void validateDownloadedFileSize(String filePath, long expectedSizeInBytes) {
        long sizeInBytesOnDisk = fileSystem.getSizeInBytes(filePath);
        if (sizeInBytesOnDisk != expectedSizeInBytes) {
            throw new FileBackupException("The downloaded size of " + filePath + " is not what was expected (was "
                                          + sizeInBytesOnDisk + ", expected " + expectedSizeInBytes + ")");
        } else {
            log.info("Filesize check for {} passed successfully", filePath);
        }
    }
}
