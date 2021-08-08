package dev.codesoapbox.backity.core.files.downloading.infrastructure.repositories;

import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.backity.core.files.downloading.domain.repositories.EnqueuedFileDownloadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EnqueuedFileDownloadJpaRepository implements EnqueuedFileDownloadRepository {

    private final EnqueuedFileDownloadSpringRepository springRepository;

    @Override
    public Optional<EnqueuedFileDownload> findOldestUnprocessed() {
        return springRepository.findFirstByDownloadedFalseAndFailedFalseOrderByDateCreatedAsc();
    }

    @Override
    public Page<EnqueuedFileDownload> findAllOrderedByDateCreatedAscending(Pageable pageable) {
        return springRepository.findAllByOrderByDateCreatedAsc(pageable);
    }
}
