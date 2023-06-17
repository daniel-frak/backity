package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameFileDetailsJpaEntitySpringRepository extends JpaRepository<GameFileDetailsJpaEntity, UUID> {

    // @TODO Remove unnecessary duplications of findAllByStatus
    @Query("SELECT f FROM GameFileDetails f" +
            " WHERE f.backupDetails.status = dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus.ENQUEUED")
    Page<GameFileDetailsJpaEntity> findAllWaitingForDownload(Pageable pageable);

    Optional<GameFileDetailsJpaEntity> findByBackupDetailsStatus(FileBackupStatus status);

    @Query("SELECT f FROM GameFileDetails f" +
            " WHERE f.backupDetails.status IN (" +
            " dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus.SUCCESS," +
            " dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus.FAILED" +
            ")")
    Page<GameFileDetailsJpaEntity> findAllProcessed(Pageable pageable);

    boolean existsBySourceFileDetailsUrlAndSourceFileDetailsVersion(String url, String version);

    Page<GameFileDetailsJpaEntity> findAllByBackupDetailsStatus(Pageable pageable, FileBackupStatus status);

    List<GameFileDetailsJpaEntity> findAllByGameId(UUID gameId);
}
