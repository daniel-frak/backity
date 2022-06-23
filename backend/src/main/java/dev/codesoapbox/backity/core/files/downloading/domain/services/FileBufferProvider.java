package dev.codesoapbox.backity.core.files.downloading.domain.services;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public interface FileBufferProvider {

    Flux<DataBuffer> getFileBuffer(String gameFileUrl, AtomicReference<String> targetFileName, AtomicLong size,
                                   DownloadProgress progress);
}
