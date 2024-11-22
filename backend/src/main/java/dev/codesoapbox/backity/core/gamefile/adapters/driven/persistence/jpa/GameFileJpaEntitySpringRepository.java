package dev.codesoapbox.backity.core.gamefile.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameFileJpaEntitySpringRepository extends JpaRepository<GameFileJpaEntity, UUID> {

    @Query("SELECT f FROM GameFile f" +
            " WHERE f.fileBackup.status =" +
            " dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus.ENQUEUED")
    Page<GameFileJpaEntity> findAllWaitingForDownload(Pageable pageable);

    Optional<GameFileJpaEntity> findByFileBackupStatus(FileBackupStatus status);

    @Query("SELECT f FROM GameFile f" +
            " WHERE f.fileBackup.status IN (" +
            " dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus.SUCCESS," +
            " dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus.FAILED" +
            ")")
    Page<GameFileJpaEntity> findAllProcessed(Pageable pageable);

    boolean existsByGameProviderFileUrlAndGameProviderFileVersion(String url, String version);

    Page<GameFileJpaEntity> findAllByFileBackupStatus(Pageable pageable, FileBackupStatus status);

    List<GameFileJpaEntity> findAllByGameId(UUID gameId);
}
