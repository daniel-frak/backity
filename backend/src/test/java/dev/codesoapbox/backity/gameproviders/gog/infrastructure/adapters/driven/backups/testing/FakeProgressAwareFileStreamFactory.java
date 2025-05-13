package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.backups.testing;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.BackupProgress;
import dev.codesoapbox.backity.gameproviders.gog.application.TrackableFileStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;

import java.time.Clock;

@RequiredArgsConstructor
public class FakeProgressAwareFileStreamFactory {

    private final Clock clock;

    public TrackableFileStream create(BackupProgress progress, String data) {
        progress.initializeTracking(data.getBytes().length, clock);
        byte[] bytes = data.getBytes();
        DefaultDataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(bytes);

        return new TrackableFileStream(Flux.just(dataBuffer), progress);
    }
}
