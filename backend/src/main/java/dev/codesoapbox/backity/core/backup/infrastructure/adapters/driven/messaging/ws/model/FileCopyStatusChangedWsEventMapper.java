package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.messaging.ws.model.BackupTargetValueObjectWsDtoMapper;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.messaging.ws.model.filecopy.FileCopyValueObjectWsDtoMapper;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.messaging.ws.model.sourcefile.SourceFileValueObjectWsDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.model.SharedWsDtoMapperConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SharedWsDtoMapperConfig.class,
        uses = {
                FileCopyValueObjectWsDtoMapper.class,
                BackupTargetValueObjectWsDtoMapper.class,
                SourceFileValueObjectWsDtoMapper.class,
        })
public abstract class FileCopyStatusChangedWsEventMapper {

    @Mapping(target = "failedReason", ignore = true)
    public abstract FileCopyStatusChangedWsEvent toWsEvent(FileBackupFinishedEvent event);

    @Mapping(target = "newStatus", expression = "java( statusFailed() )")
    public abstract FileCopyStatusChangedWsEvent toWsEvent(FileBackupFailedEvent event);

    protected String statusFailed() {
        return FileCopyStatus.FAILED.name();
    }

    @Mapping(target = "fileCopyId", source = "fileCopyId")
    @Mapping(target = "fileCopyNaturalId", source = "fileCopyNaturalId")
    @Mapping(target = "newStatus", expression = "java( statusInProgress() )")
    @Mapping(target = "failedReason", ignore = true)
    @BeanMapping(ignoreByDefault = true)
    public abstract FileCopyStatusChangedWsEvent toWsEvent(FileBackupStartedEvent event);

    protected abstract FileCopyNaturalIdWsDto toWsDto(FileCopyNaturalId fileCopyNaturalId);

    protected String statusInProgress() {
        return FileCopyStatus.IN_PROGRESS.name();
    }
}
