package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BackupTargetSpringRepository extends JpaRepository<BackupTargetJpaEntity, UUID> {

    List<BackupTargetJpaEntity> findAllByIdIn(List<UUID> uuids, Sort sort);
}
