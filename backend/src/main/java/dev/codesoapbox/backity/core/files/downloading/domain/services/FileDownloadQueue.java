package dev.codesoapbox.backity.core.files.downloading.domain.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import dev.codesoapbox.backity.core.files.downloading.domain.model.DownloadStatus;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.backity.core.files.downloading.domain.repositories.EnqueuedFileDownloadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
public class FileDownloadQueue {

    private final DiscoveredFileRepository discoveredFileRepository;
    private final EnqueuedFileDownloadRepository downloadRepository;
    private final FileDownloadMessageService messageService;

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

    public Optional<EnqueuedFileDownload> getOldestWaiting() {
        return downloadRepository.findOldestWaiting();
    }

    public void acknowledgeSuccess(EnqueuedFileDownload enqueuedFileDownload) {
        enqueuedFileDownload.setStatus(DownloadStatus.DOWNLOADED);
        downloadRepository.save(enqueuedFileDownload);
        messageService.sendDownloadFinished(enqueuedFileDownload);
    }

    public void acknowledgeFailed(EnqueuedFileDownload enqueuedFileDownload, String reason) {
        enqueuedFileDownload.fail(reason);
        downloadRepository.save(enqueuedFileDownload);
        messageService.sendDownloadFinished(enqueuedFileDownload);
    }

    public Page<EnqueuedFileDownload> findAllQueued(Pageable pageable) {
        return downloadRepository.findAllWaiting(pageable);
    }

    public Optional<EnqueuedFileDownload> findCurrentlyDownloading() {
        return downloadRepository.findCurrentlyDownloading();
    }

    public void markInProgress(EnqueuedFileDownload enqueuedFileDownload) {
        enqueuedFileDownload.setStatus(DownloadStatus.IN_PROGRESS);
        downloadRepository.save(enqueuedFileDownload);
        messageService.sendDownloadStarted(enqueuedFileDownload);
    }

    public Page<EnqueuedFileDownload> findAllProcessed(Pageable pageable) {
        return downloadRepository.findAllProcessed(pageable);
    }
}
