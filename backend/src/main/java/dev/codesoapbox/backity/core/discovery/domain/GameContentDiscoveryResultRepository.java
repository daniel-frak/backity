package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;

import java.util.Collection;
import java.util.List;

public interface GameContentDiscoveryResultRepository {

    void save(GameContentDiscoveryResult discoveryResult);

    List<GameContentDiscoveryResult> findAllByGameProviderIdIn(Collection<GameProviderId> gameProviderIds);
}
