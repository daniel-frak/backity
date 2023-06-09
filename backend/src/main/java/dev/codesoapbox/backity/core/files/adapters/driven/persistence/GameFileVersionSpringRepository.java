package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GameFileVersionSpringRepository extends JpaRepository<GameFileVersionBackup, Long> {

    // @TODO Remove unnecessary duplications of findAllByStatus
    @Query("SELECT f FROM GameFileVersionBackup f" +
            " WHERE f.status = dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus.ENQUEUED" +
            " ORDER BY f.dateCreated ASC")
    Page<GameFileVersionBackup> findAllWaitingForDownload(Pageable pageable);

    Optional<GameFileVersionBackup> findByStatus(FileBackupStatus status);

    @Query("SELECT f FROM GameFileVersionBackup f" +
            " WHERE f.status IN (" +
            " dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus.SUCCESS," +
            " dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus.FAILED" +
            ")" +
            " ORDER BY f.dateCreated ASC")
    Page<GameFileVersionBackup> findAllProcessed(Pageable pageable);

    boolean existsByUrlAndVersion(String url, String version);

    Page<GameFileVersionBackup> findAllByStatus(Pageable pageable, FileBackupStatus status);
}
