package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameFileVersionSpringRepository extends JpaRepository<JpaGameFileVersionBackup, Long> {

    // @TODO Remove unnecessary duplications of findAllByStatus
    @Query("SELECT f FROM GameFileVersionBackup f" +
            " WHERE f.status = dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus.ENQUEUED" +
            " ORDER BY f.dateCreated ASC")
    Page<JpaGameFileVersionBackup> findAllWaitingForDownload(Pageable pageable);

    Optional<JpaGameFileVersionBackup> findByStatus(FileBackupStatus status);

    @Query("SELECT f FROM GameFileVersionBackup f" +
            " WHERE f.status IN (" +
            " dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus.SUCCESS," +
            " dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus.FAILED" +
            ")" +
            " ORDER BY f.dateCreated ASC")
    Page<JpaGameFileVersionBackup> findAllProcessed(Pageable pageable);

    boolean existsByUrlAndVersion(String url, String version);

    Page<JpaGameFileVersionBackup> findAllByStatus(Pageable pageable, FileBackupStatus status);

    List<JpaGameFileVersionBackup> findAllByGameId(String gameId);
}
