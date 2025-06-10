package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameContentDiscoveryResultJpaRepository implements GameContentDiscoveryResultRepository {

    private final GameContentDiscoveryResultSpringRepository springRepository;
    private final GameContentDiscoveryResultJpaEntityMapper entityMapper;

    @Transactional
    @Override
    public void save(GameContentDiscoveryResult discoveryResult) {
        GameContentDiscoveryResultJpaEntity entity = entityMapper.toEntity(discoveryResult);

        springRepository.save(entity);
    }

    @Override
    public List<GameContentDiscoveryResult> findAllByGameProviderIdIn(Collection<GameProviderId> gameProviderIds) {
        Set<String> gameProviderIdStrings = gameProviderIds.stream()
                .map(GameProviderId::value)
                .collect(Collectors.toSet());

        return springRepository.findAllByGameProviderIdIn(gameProviderIdStrings).stream()
                .map(entityMapper::toDomain)
                .toList();
    }
}
