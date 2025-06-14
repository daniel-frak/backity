package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

public record TrackableFileStream(
        Flux<DataBuffer> dataStream,
        DownloadProgress progress
) {
}
