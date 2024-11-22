package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.gamefile.domain.GameProviderFile;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface FileDiscoveredWsEventMapper {

    FileDiscoveredWsEvent toWsEvent(GameProviderFile domain);
}
