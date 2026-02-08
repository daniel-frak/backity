package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface SourceFileJpaEntitySpringRepository extends JpaRepository<SourceFileJpaEntity, UUID> {

    boolean existsByUrlAndVersion(String url, String version);

    List<SourceFileJpaEntity> findAllByGameId(UUID gameId);

    List<SourceFileJpaEntity> findAllByIdIn(Collection<UUID> ids);
}
