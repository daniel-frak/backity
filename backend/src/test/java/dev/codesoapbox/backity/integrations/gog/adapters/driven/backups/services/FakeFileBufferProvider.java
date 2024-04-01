package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.backup.domain.BackupProgress;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class FakeFileBufferProvider implements FileBufferProvider {

    Map<String, String> stringDataToDownloadByUrl = new HashMap<>();

    public void mockDataForDownload(GameFileDetails gameFileDetails, String testData) {
        String url = gameFileDetails.getSourceFileDetails().url();
        stringDataToDownloadByUrl.put(url, testData);
    }

    @Override
    public Flux<DataBuffer> getFileBuffer(String gameFileUrl, BackupProgress progress) {
        String data = stringDataToDownloadByUrl.get(gameFileUrl);
        progress.startTracking(data.getBytes().length);
        byte[] bytes = data.getBytes();
        DefaultDataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(bytes);
        return Flux.just(dataBuffer);
    }
}
