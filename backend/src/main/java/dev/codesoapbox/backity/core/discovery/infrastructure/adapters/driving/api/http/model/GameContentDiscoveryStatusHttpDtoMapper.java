package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryStatus;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgress;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.ProgressHttpDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class GameContentDiscoveryStatusHttpDtoMapper {

    public abstract GameContentDiscoveryStatusHttpDto toDto(GameContentDiscoveryStatus domain);

    protected String getValue(GameProviderId id) {
        return id.value();
    }

    @Mapping(target = "timeLeftSeconds", source = "timeLeft.seconds")
    protected abstract ProgressHttpDto toProgressDto(GameContentDiscoveryProgress domain);
}
