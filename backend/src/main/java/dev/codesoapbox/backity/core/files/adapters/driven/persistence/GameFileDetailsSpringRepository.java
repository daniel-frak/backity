package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameFileDetailsSpringRepository extends JpaRepository<JpaGameFileDetails, Long> {

    // @TODO Remove unnecessary duplications of findAllByStatus
    @Query("SELECT f FROM GameFileDetails f" +
            " WHERE f.backupStatus = dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus.ENQUEUED" +
            " ORDER BY f.dateCreated ASC")
    Page<JpaGameFileDetails> findAllWaitingForDownload(Pageable pageable);

    Optional<JpaGameFileDetails> findByBackupStatus(FileBackupStatus status);

    @Query("SELECT f FROM GameFileDetails f" +
            " WHERE f.backupStatus IN (" +
            " dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus.SUCCESS," +
            " dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus.FAILED" +
            ")" +
            " ORDER BY f.dateCreated ASC")
    Page<JpaGameFileDetails> findAllProcessed(Pageable pageable);

    boolean existsByUrlAndVersion(String url, String version);

    Page<JpaGameFileDetails> findAllByBackupStatus(Pageable pageable, FileBackupStatus status);

    List<JpaGameFileDetails> findAllByGameId(String gameId);
}
