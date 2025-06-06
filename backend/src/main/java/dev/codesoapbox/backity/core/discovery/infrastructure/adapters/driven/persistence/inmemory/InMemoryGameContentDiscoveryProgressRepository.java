package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.inmemory;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgress;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgressRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryGameContentDiscoveryProgressRepository implements GameContentDiscoveryProgressRepository {

    private final Map<GameProviderId, GameContentDiscoveryProgress> discoveryProgressesByGameProviderId =
            new ConcurrentHashMap<>();

    @Override
    public void save(GameContentDiscoveryProgress progress) {
        discoveryProgressesByGameProviderId.put(progress.gameProviderId(), progress);
    }

    @Override
    public void deleteByGameProviderId(GameProviderId gameProviderId) {
        discoveryProgressesByGameProviderId.remove(gameProviderId);
    }

    @Override
    public List<GameContentDiscoveryProgress> findAllByGameProviderIdIn(Collection<GameProviderId> gameProviderIds) {
        return gameProviderIds.stream()
                .map(discoveryProgressesByGameProviderId::get)
                .filter(Objects::nonNull)
                .toList();
    }
}
