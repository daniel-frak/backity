package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.downloading.model.DownloadStatus;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GameFileVersionSpringRepository extends JpaRepository<GameFileVersion, Long> {

    // @TODO Remove unnecessary duplications of findAllByStatus
    @Query("SELECT f FROM GameFileVersion f" +
            " WHERE f.status = dev.codesoapbox.backity.core.files.domain.downloading.model.DownloadStatus.WAITING" +
            " ORDER BY f.dateCreated ASC")
    Page<GameFileVersion> findAllWaitingForDownload(Pageable pageable);

    Optional<GameFileVersion> findByStatus(DownloadStatus status);

    @Query("SELECT f FROM GameFileVersion f" +
            " WHERE f.status IN (" +
            " dev.codesoapbox.backity.core.files.domain.downloading.model.DownloadStatus.DOWNLOADED," +
            " dev.codesoapbox.backity.core.files.domain.downloading.model.DownloadStatus.FAILED" +
            ")" +
            " ORDER BY f.dateCreated ASC")
    Page<GameFileVersion> findAllProcessed(Pageable pageable);

    boolean existsByUrlAndVersion(String url, String version);

    Page<GameFileVersion> findAllByStatus(Pageable pageable, DownloadStatus status);
}
