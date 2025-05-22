package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.exceptions.FileDownloadException;
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

    public void downloadFile(StorageSolution storageSolution, TrackableFileStream trackableFileStream,
                             GameFile gameFile, String filePath)
            throws IOException {
        DownloadProgress progress = trackableFileStream.progress();
        writeToDisk(storageSolution, trackableFileStream.dataStream(), filePath, progress);

        log.info("Downloaded file {} to {}", gameFile, filePath);

        validateDownloadedFileSize(storageSolution, filePath, progress.getContentLengthBytes());
    }

    private void writeToDisk(StorageSolution storageSolution, Flux<DataBuffer> dataBufferFlux, String filePath,
                             DownloadProgress progress)
            throws IOException {
        try (OutputStream outputStream = storageSolution.getOutputStream(filePath)) {
            DataBufferUtils
                    .write(dataBufferFlux, progress.track(outputStream))
                    .map(DataBufferUtils::release)
                    .blockLast();
        }
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
}
