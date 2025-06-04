package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface GameFileJpaEntitySpringRepository extends JpaRepository<GameFileJpaEntity, UUID> {

    boolean existsByFileSourceUrlAndFileSourceVersion(String url, String version);

    List<GameFileJpaEntity> findAllByGameId(UUID gameId);

    List<GameFileJpaEntity> findAllByIdIn(Collection<UUID> ids);
}
