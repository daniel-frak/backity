package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.gamefiledetails.domain.SourceFileDetails;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileDiscoveredMessageMapper {

    public abstract FileDiscoveredWsMessage toMessage(SourceFileDetails domain);
}
