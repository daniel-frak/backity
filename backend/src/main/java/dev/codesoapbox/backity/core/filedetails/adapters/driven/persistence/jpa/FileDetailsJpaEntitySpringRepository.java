package dev.codesoapbox.backity.core.filedetails.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.filedetails.domain.FileBackupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileDetailsJpaEntitySpringRepository extends JpaRepository<FileDetailsJpaEntity, UUID> {

    @Query("SELECT f FROM FileDetails f" +
            " WHERE f.backupDetails.status =" +
            " dev.codesoapbox.backity.core.filedetails.domain.FileBackupStatus.ENQUEUED")
    Page<FileDetailsJpaEntity> findAllWaitingForDownload(Pageable pageable);

    Optional<FileDetailsJpaEntity> findByBackupDetailsStatus(FileBackupStatus status);

    @Query("SELECT f FROM FileDetails f" +
            " WHERE f.backupDetails.status IN (" +
            " dev.codesoapbox.backity.core.filedetails.domain.FileBackupStatus.SUCCESS," +
            " dev.codesoapbox.backity.core.filedetails.domain.FileBackupStatus.FAILED" +
            ")")
    Page<FileDetailsJpaEntity> findAllProcessed(Pageable pageable);

    boolean existsBySourceFileDetailsUrlAndSourceFileDetailsVersion(String url, String version);

    Page<FileDetailsJpaEntity> findAllByBackupDetailsStatus(Pageable pageable, FileBackupStatus status);

    List<FileDetailsJpaEntity> findAllByGameId(UUID gameId);
}
