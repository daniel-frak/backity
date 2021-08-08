package dev.codesoapbox.backity.files.downloading.domain.repositories;

import dev.codesoapbox.backity.files.downloading.domain.model.EnqueuedFileDownload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EnqueuedFileDownloadRepository {

    Optional<EnqueuedFileDownload> findOldestUnprocessed();

    Page<EnqueuedFileDownload> findAllOrderedByDateCreatedAscending(Pageable pageable);
}
