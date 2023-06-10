package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameFileVersionSpringRepository extends JpaRepository<JpaGameFileVersion, Long> {

    // @TODO Remove unnecessary duplications of findAllByStatus
    @Query("SELECT f FROM GameFileVersion f" +
            " WHERE f.backupStatus = dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus.ENQUEUED" +
            " ORDER BY f.dateCreated ASC")
    Page<JpaGameFileVersion> findAllWaitingForDownload(Pageable pageable);

    Optional<JpaGameFileVersion> findByBackupStatus(FileBackupStatus status);

    @Query("SELECT f FROM GameFileVersion f" +
            " WHERE f.backupStatus IN (" +
            " dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus.SUCCESS," +
            " dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus.FAILED" +
            ")" +
            " ORDER BY f.dateCreated ASC")
    Page<JpaGameFileVersion> findAllProcessed(Pageable pageable);

    boolean existsByUrlAndVersion(String url, String version);

    Page<JpaGameFileVersion> findAllByBackupStatus(Pageable pageable, FileBackupStatus status);

    List<JpaGameFileVersion> findAllByGameId(String gameId);
}
