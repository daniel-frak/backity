package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;

import java.util.Collection;
import java.util.List;

public interface GameContentDiscoveryProgressRepository {

    void save(GameContentDiscoveryProgress progress);

    void deleteByGameProviderId(GameProviderId gameProviderId);

    List<GameContentDiscoveryProgress> findAllByGameProviderIdIn(Collection<GameProviderId> gameProviderIds);
}
