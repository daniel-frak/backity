package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services;

import dev.codesoapbox.backity.core.files.domain.downloading.services.DownloadProgress;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

public interface FileBufferProvider {

    Flux<DataBuffer> getFileBuffer(String gameFileUrl,
                                   DownloadProgress progress);
}
