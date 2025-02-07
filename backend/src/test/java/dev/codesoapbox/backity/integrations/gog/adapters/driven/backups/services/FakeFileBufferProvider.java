package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.BackupProgress;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class FakeFileBufferProvider implements FileBufferProvider {

    private final Clock clock;

    Map<String, String> stringDataToDownloadByUrl = new HashMap<>();

    public void mockDataForDownload(GameFile gameFile, String testData) {
        String url = gameFile.getGameProviderFile().url();
        stringDataToDownloadByUrl.put(url, testData);
    }

    @Override
    public Flux<DataBuffer> getFileBuffer(String fileUrl, BackupProgress progress) {
        String data = stringDataToDownloadByUrl.get(fileUrl);
        progress.initializeTracking(data.getBytes().length, clock);
        byte[] bytes = data.getBytes();
        DefaultDataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(bytes);
        return Flux.just(dataBuffer);
    }
}
