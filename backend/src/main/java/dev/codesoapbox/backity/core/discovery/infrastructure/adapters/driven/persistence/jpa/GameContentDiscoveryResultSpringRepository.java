package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface GameContentDiscoveryResultSpringRepository
        extends JpaRepository<GameContentDiscoveryResultJpaEntity, String> {

    List<GameContentDiscoveryResultJpaEntity> findAllByGameProviderIdIn(Collection<String> gameProviderIds);
}
