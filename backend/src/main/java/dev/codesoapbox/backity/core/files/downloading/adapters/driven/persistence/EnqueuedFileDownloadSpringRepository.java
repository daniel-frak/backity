package dev.codesoapbox.backity.core.files.downloading.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.downloading.domain.model.DownloadStatus;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EnqueuedFileDownloadSpringRepository extends JpaRepository<EnqueuedFileDownload, Long> {

    @Query("SELECT f FROM EnqueuedFileDownload f" +
            " WHERE f.status = dev.codesoapbox.backity.core.files.downloading.domain.model.DownloadStatus.WAITING" +
            " ORDER BY f.dateCreated ASC")
    Page<EnqueuedFileDownload> findAllQueued(Pageable pageable);

    Optional<EnqueuedFileDownload> findByStatus(DownloadStatus status);

    @Query("SELECT f FROM EnqueuedFileDownload f" +
            " WHERE f.status IN (" +
            " dev.codesoapbox.backity.core.files.downloading.domain.model.DownloadStatus.DOWNLOADED," +
            " dev.codesoapbox.backity.core.files.downloading.domain.model.DownloadStatus.FAILED" +
            ")" +
            " ORDER BY f.dateCreated ASC")
    Page<EnqueuedFileDownload> findAllProcessed(Pageable pageable);
}
