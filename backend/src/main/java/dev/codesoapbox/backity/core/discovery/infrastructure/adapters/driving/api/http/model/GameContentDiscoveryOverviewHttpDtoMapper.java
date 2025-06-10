package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryOverview;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class GameContentDiscoveryOverviewHttpDtoMapper {

    public abstract GameContentDiscoveryOverviewHttpDto toDto(GameContentDiscoveryOverview domain);

    protected String getValue(GameProviderId id) {
        return id.value();
    }

    @Mapping(target = "timeLeftSeconds", source = "timeLeft.seconds")
    protected abstract GameContentDiscoveryProgressHttpDto toProgressDto(GameContentDiscoveryProgress domain);
}
