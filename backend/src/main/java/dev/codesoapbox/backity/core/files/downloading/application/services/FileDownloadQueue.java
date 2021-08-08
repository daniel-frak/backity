package dev.codesoapbox.backity.core.files.downloading.application.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.backity.core.files.downloading.infrastructure.repositories.EnqueuedFileDownloadSpringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileDownloadQueue {

    private final EnqueuedFileDownloadSpringRepository repository;

    public void enqueue(DiscoveredFile discoveredFile) {
        EnqueuedFileDownload enqueuedFileDownload = createEnqueuedFileDownload(discoveredFile);
        repository.save(enqueuedFileDownload);
    }

    private EnqueuedFileDownload createEnqueuedFileDownload(DiscoveredFile discoveredFile) {
        var enqueuedFileDownload = new EnqueuedFileDownload();
        enqueuedFileDownload.setSource(discoveredFile.getSource());
        enqueuedFileDownload.setUrl(discoveredFile.getId().getUrl());
        enqueuedFileDownload.setName(discoveredFile.getName());
        enqueuedFileDownload.setGameTitle(discoveredFile.getGameTitle());
        enqueuedFileDownload.setVersion(discoveredFile.getId().getVersion());
        enqueuedFileDownload.setSize(discoveredFile.getSize());

        return enqueuedFileDownload;
    }

    public Optional<EnqueuedFileDownload> getOldestUnprocessed() {
        return repository.findFirstByDownloadedFalseAndFailedFalseOrderByDateCreatedAsc();
    }

    public void acknowledgeSuccess(EnqueuedFileDownload enqueuedFileDownload) {
        enqueuedFileDownload.setDownloaded(true);
        repository.save(enqueuedFileDownload);
    }

    public void acknowledgeFailed(EnqueuedFileDownload enqueuedFileDownload) {
        enqueuedFileDownload.setFailed(true);
        repository.save(enqueuedFileDownload);
    }

    public Page<EnqueuedFileDownload> getQueueItems(Pageable pageable) {
        return repository.findAllByOrderByDateCreatedAsc(pageable);
    }
}
