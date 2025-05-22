package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileCopySpringRepository extends JpaRepository<FileCopyJpaEntity, UUID> {

    FileCopyJpaEntity getByNaturalIdGameFileIdAndNaturalIdBackupTargetId(UUID gameFileId, UUID backupTargetId);

    Optional<FileCopyJpaEntity> findByNaturalIdGameFileIdAndNaturalIdBackupTargetId(
            UUID gameFileId, UUID backupTargetId);

    Optional<FileCopyJpaEntity> findByStatus(FileCopyStatus status);

    Page<FileCopyJpaEntity> findAllByStatusIn(Pageable pageable, List<FileCopyStatus> status);

    List<FileCopyJpaEntity> findAllByNaturalIdGameFileId(UUID gameFileId);
}
