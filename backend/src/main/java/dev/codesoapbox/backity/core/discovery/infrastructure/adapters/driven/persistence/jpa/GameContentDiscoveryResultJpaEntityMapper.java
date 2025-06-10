package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class GameContentDiscoveryResultJpaEntityMapper {

    public abstract GameContentDiscoveryResultJpaEntity toEntity(GameContentDiscoveryResult domain);

    protected String getValue(GameProviderId id) {
        return id.value();
    }

    public abstract GameContentDiscoveryResult toDomain(GameContentDiscoveryResultJpaEntity entity);

    protected GameProviderId toGameProviderId(String value) {
        return new GameProviderId(value);
    }
}
