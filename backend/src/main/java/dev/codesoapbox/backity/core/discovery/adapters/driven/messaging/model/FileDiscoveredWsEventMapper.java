package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.filedetails.domain.SourceFileDetails;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface FileDiscoveredWsEventMapper {

    FileDiscoveredWsEvent toWsEvent(SourceFileDetails domain);
}
