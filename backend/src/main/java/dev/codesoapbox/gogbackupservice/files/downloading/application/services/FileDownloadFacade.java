package dev.codesoapbox.gogbackupservice.files.downloading.application.services;

import dev.codesoapbox.gogbackupservice.files.discovery.domain.DiscoveredFileRepository;
import dev.codesoapbox.gogbackupservice.files.downloading.domain.EnqueuedFileDownload;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
