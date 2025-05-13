package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

public record TrackableFileStream(
        Flux<DataBuffer> dataStream,
        DownloadProgress progress
) {
}
