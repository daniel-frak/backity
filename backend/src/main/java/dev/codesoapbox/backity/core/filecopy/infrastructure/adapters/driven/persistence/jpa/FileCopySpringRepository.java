package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileCopySpringRepository extends JpaRepository<FileCopyJpaEntity, UUID> {

    FileCopyJpaEntity getByNaturalIdGameFileIdAndNaturalIdBackupTargetId(UUID gameFileId, UUID backupTargetId);

    Optional<FileCopyJpaEntity> findByNaturalIdGameFileIdAndNaturalIdBackupTargetId(
            UUID gameFileId, UUID backupTargetId);

    Page<FileCopyJpaEntity> findAllByStatusIn(Pageable pageable, List<FileCopyStatus> status);

    @Query("""
            SELECT f
              FROM FileCopy f
             WHERE f.status IN ('IN_PROGRESS', 'ENQUEUED')
             ORDER BY
               CASE f.status
                 WHEN 'IN_PROGRESS'               THEN 0
                 WHEN 'ENQUEUED'                  THEN 1
                 ELSE 2
               END,
               f.dateModified ASC
            """)
    Page<FileCopyJpaEntity> findAllInProgressOrEnqueuedOrderByStatusThenDateModified(Pageable pageable);

    List<FileCopyJpaEntity> findAllByNaturalIdGameFileId(UUID gameFileId);
}
