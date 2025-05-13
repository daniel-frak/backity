package dev.codesoapbox.backity.gameproviders.gog.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.BackupProgress;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

public record TrackableFileStream(
        Flux<DataBuffer> dataStream,
        BackupProgress progress
) {
}
