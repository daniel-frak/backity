package dev.codesoapbox.backity.core.logs.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LogCreatedWsEventMapper {

    LogCreatedWsEvent toWsEvent(LogCreatedEvent domain);
}
