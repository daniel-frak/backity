package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileCopySpringRepository extends JpaRepository<FileCopyJpaEntity, UUID> {
}
