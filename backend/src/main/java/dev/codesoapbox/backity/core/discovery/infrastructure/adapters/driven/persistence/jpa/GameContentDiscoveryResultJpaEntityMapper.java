package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.persistence.jpa.BackupValueObjectJpaDtoMapper;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SharedJpaDtoMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = SharedJpaDtoMapperConfig.class,
        uses = {
                BackupValueObjectJpaDtoMapper.class
        })
public abstract class GameContentDiscoveryResultJpaEntityMapper {

    public abstract GameContentDiscoveryResultJpaEntity toEntity(GameContentDiscoveryResult domain);

    public abstract GameContentDiscoveryResult toDomain(GameContentDiscoveryResultJpaEntity entity);
}
