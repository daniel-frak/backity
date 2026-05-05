package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.messaging.ws.model.BackupTargetValueObjectWsDtoMapper;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.messaging.ws.model.filecopy.FileCopyValueObjectWsDtoMapper;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.messaging.ws.model.sourcefile.SourceFileValueObjectWsDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.model.SharedWsDtoMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SharedWsDtoMapperConfig.class,
        uses = {
                FileCopyValueObjectWsDtoMapper.class,
                BackupTargetValueObjectWsDtoMapper.class,
                SourceFileValueObjectWsDtoMapper.class,
        })
public abstract class FileCopyReplicationProgressUpdatedWsEventMapper {

    @Mapping(target = "timeLeftSeconds", source = "timeLeft.seconds")
    public abstract FileCopyReplicationProgressUpdatedWsEvent toWsEvent(FileCopyReplicationProgressChangedEvent domain);
}
