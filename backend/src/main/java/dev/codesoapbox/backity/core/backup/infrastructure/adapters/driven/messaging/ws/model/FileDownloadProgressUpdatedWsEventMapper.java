package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileDownloadProgressChangedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface FileDownloadProgressUpdatedWsEventMapper {

    @Mapping(target = "timeLeftSeconds", source = "timeLeft.seconds")
    FileDownloadProgressUpdatedWsEvent toWsEvent(FileDownloadProgressChangedEvent domain);
}
