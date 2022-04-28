package dev.codesoapbox.backity.core.files.downloading.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.downloading.domain.model.DownloadStatus;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.backity.core.files.downloading.domain.repositories.EnqueuedFileDownloadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@RequiredArgsConstructor
public class EnqueuedFileDownloadJpaRepository implements EnqueuedFileDownloadRepository {

    private final EnqueuedFileDownloadSpringRepository springRepository;

    @Override
    public Optional<EnqueuedFileDownload> findOldestUnprocessed() {
        return springRepository.findAllQueued(PageRequest.of(0, 1)).get()
                .findFirst();
    }

    @Override
    public Page<EnqueuedFileDownload> findAllQueued(Pageable pageable) {
        return springRepository.findAllQueued(pageable);
    }

    @Override
    public EnqueuedFileDownload save(EnqueuedFileDownload enqueuedFileDownload) {
        return springRepository.save(enqueuedFileDownload);
    }

    @Override
    public Optional<EnqueuedFileDownload> findCurrentlyDownloading() {
        return springRepository.findByStatus(DownloadStatus.IN_PROGRESS);
    }

    @Override
    public Page<EnqueuedFileDownload> findAllProcessed(Pageable pageable) {
        return springRepository.findAllProcessed(pageable);
    }
}
