package dev.codesoapbox.gogbackupservice.files.downloading.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EnqueuedFileDownloadRepository {

    Optional<EnqueuedFileDownload> findOldestUnprocessed();

    Page<EnqueuedFileDownload> findAllOrderedByDateCreatedAscending(Pageable pageable);
}
