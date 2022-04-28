package dev.codesoapbox.backity.core.files.downloading.domain.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import dev.codesoapbox.backity.core.files.downloading.domain.model.DownloadStatus;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.backity.core.files.downloading.domain.model.FileDownloadMessageTopics;
import dev.codesoapbox.backity.core.files.downloading.domain.repositories.EnqueuedFileDownloadRepository;
import dev.codesoapbox.backity.core.shared.domain.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
public class FileDownloadQueue {

    private final DiscoveredFileRepository discoveredFileRepository;
    private final EnqueuedFileDownloadRepository downloadRepository;
    private final MessageService messageService;

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
        enqueuedFileDownload.setStatus(DownloadStatus.DOWNLOADED);
        downloadRepository.save(enqueuedFileDownload);
        messageService.sendMessage(FileDownloadMessageTopics.DOWNLOAD_FINISHED.toString(), enqueuedFileDownload);
    }

    public void acknowledgeFailed(EnqueuedFileDownload enqueuedFileDownload, String reason) {
        enqueuedFileDownload.fail(reason);
        downloadRepository.save(enqueuedFileDownload);
        messageService.sendMessage(FileDownloadMessageTopics.DOWNLOAD_FINISHED.toString(), enqueuedFileDownload);
    }

    public Page<EnqueuedFileDownload> findAllQueued(Pageable pageable) {
        return downloadRepository.findAllQueued(pageable);
    }

    public Optional<EnqueuedFileDownload> findCurrentlyDownloading() {
        return downloadRepository.findCurrentlyDownloading();
    }

    public void markInProgress(EnqueuedFileDownload enqueuedFileDownload) {
        enqueuedFileDownload.setStatus(DownloadStatus.IN_PROGRESS);
        downloadRepository.save(enqueuedFileDownload);
        messageService.sendMessage(FileDownloadMessageTopics.DOWNLOAD_STARTED.toString(), enqueuedFileDownload);
    }

    public Page<EnqueuedFileDownload> findAllProcessed(Pageable pageable) {
        return downloadRepository.findAllProcessed(pageable);
    }
}
