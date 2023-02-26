package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.downloading.model.FileStatus;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GameFileVersionSpringRepository extends JpaRepository<GameFileVersion, Long> {

    // @TODO Remove unnecessary duplications of findAllByStatus
    @Query("SELECT f FROM GameFileVersion f" +
            " WHERE f.status = dev.codesoapbox.backity.core.files.domain.downloading.model.FileStatus.ENQUEUED_FOR_DOWNLOAD" +
            " ORDER BY f.dateCreated ASC")
    Page<GameFileVersion> findAllWaitingForDownload(Pageable pageable);

    Optional<GameFileVersion> findByStatus(FileStatus status);

    @Query("SELECT f FROM GameFileVersion f" +
            " WHERE f.status IN (" +
            " dev.codesoapbox.backity.core.files.domain.downloading.model.FileStatus.DOWNLOADED," +
            " dev.codesoapbox.backity.core.files.domain.downloading.model.FileStatus.DOWNLOAD_FAILED" +
            ")" +
            " ORDER BY f.dateCreated ASC")
    Page<GameFileVersion> findAllProcessed(Pageable pageable);

    boolean existsByUrlAndVersion(String url, String version);

    Page<GameFileVersion> findAllByStatus(Pageable pageable, FileStatus status);
}
