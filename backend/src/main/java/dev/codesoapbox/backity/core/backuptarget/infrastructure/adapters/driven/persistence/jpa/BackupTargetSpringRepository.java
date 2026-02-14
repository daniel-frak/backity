package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BackupTargetSpringRepository extends JpaRepository<BackupTargetJpaEntity, UUID> {
}
