package dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LogCreatedWsEventMapper {

    LogCreatedWsEvent toWsEvent(LogCreatedEvent domain);
}
