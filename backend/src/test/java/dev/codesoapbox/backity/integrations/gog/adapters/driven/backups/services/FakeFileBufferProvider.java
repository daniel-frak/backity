package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.backup.domain.BackupProgress;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
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

    public void mockDataForDownload(FileDetails fileDetails, String testData) {
        String url = fileDetails.getSourceFileDetails().url();
        stringDataToDownloadByUrl.put(url, testData);
    }

    @Override
    public Flux<DataBuffer> getFileBuffer(String fileUrl, BackupProgress progress) {
        String data = stringDataToDownloadByUrl.get(fileUrl);
        progress.startTracking(data.getBytes().length);
        byte[] bytes = data.getBytes();
        DefaultDataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(bytes);
        return Flux.just(dataBuffer);
    }
}
