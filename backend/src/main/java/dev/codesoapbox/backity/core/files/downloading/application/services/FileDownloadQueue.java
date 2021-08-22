package dev.codesoapbox.backity.core.files.downloading.application.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.backity.core.files.downloading.domain.repositories.EnqueuedFileDownloadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileDownloadQueue {

    private final DiscoveredFileRepository discoveredFileRepository;
    private final EnqueuedFileDownloadRepository downloadRepository;

    @Transactional
    public void enqueue(DiscoveredFile discoveredFile) {
        EnqueuedFileDownload enqueuedFileDownload = createEnqueuedFileDownload(discoveredFile);
        downloadRepository.save(enqueuedFileDownload);

        discoveredFile.setEnqueued(true);
        discoveredFileRepository.save(discoveredFile);
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
        return downloadRepository.findOldestUnprocessed();
    }

    public void acknowledgeSuccess(EnqueuedFileDownload enqueuedFileDownload) {
        enqueuedFileDownload.setDownloaded(true);
        downloadRepository.save(enqueuedFileDownload);
    }

    public void acknowledgeFailed(EnqueuedFileDownload enqueuedFileDownload, String reason) {
        enqueuedFileDownload.setFailed(true);
        enqueuedFileDownload.setFailedReason(reason);
        downloadRepository.save(enqueuedFileDownload);
    }

    public Page<EnqueuedFileDownload> getQueueItems(Pageable pageable) {
        return downloadRepository.findAllOrderedByDateCreatedAscending(pageable);
    }
}
