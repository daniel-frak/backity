package dev.codesoapbox.backity.core.files.downloading.application.services;

import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileDownloadFacade {

    private final DiscoveredFileRepository discoveredFileRepository;
    private final FileDownloadQueue fileDownloadQueue;

    public Page<EnqueuedFileDownload> getQueueItems(Pageable pageable) {
        return fileDownloadQueue.getQueueItems(pageable);
    }

    public void download(UUID discoveredFileUniqueId) {
        discoveredFileRepository.findByUniqueId(discoveredFileUniqueId)
                .ifPresentOrElse(fileDownloadQueue::enqueue, () -> {
                    throw new IllegalArgumentException("Discovered file not found: " + discoveredFileUniqueId);
                });
    }
}