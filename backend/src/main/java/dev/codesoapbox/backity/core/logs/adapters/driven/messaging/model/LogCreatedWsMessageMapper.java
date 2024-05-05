package dev.codesoapbox.backity.core.logs.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedMessage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LogCreatedWsMessageMapper {

    LogCreatedWsMessage toWsMessage(LogCreatedMessage domain);
}
