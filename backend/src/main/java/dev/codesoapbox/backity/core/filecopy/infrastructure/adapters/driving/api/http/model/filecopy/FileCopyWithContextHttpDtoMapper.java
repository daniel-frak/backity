package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetName;
import dev.codesoapbox.backity.core.filecopy.application.usecases.FileCopyWithContext;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyFailureReason;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.game.domain.GameTitle;
import dev.codesoapbox.backity.core.sourcefile.domain.FileSize;
import dev.codesoapbox.backity.core.sourcefile.domain.FileTitle;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.ProgressHttpDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class FileCopyWithContextHttpDtoMapper {

    public abstract FileCopyWithContextHttpDto toDto(FileCopyWithContext fileCopyWithContext);

    @Mapping(target = "timeLeftSeconds", source = "timeLeft.seconds")
    protected abstract ProgressHttpDto toDto(FileCopyReplicationProgress progress);

    protected String getValue(FileCopyId id) {
        return id.value().toString();
    }

    protected String getValue(GameTitle title) {
        return title.value();
    }

    protected String getValue(FileTitle fileTitle) {
        return fileTitle.value();
    }

    protected String getValue(BackupTargetName name) {
        return name.value();
    }

    protected String getValue(SourceFileId id) {
        return id.value().toString();
    }

    protected String getValue(BackupTargetId id) {
        return id.value().toString();
    }

    protected String getValue(GameProviderId id) {
        return id.value();
    }

    protected String getValue(FileSize fileSize) {
        return fileSize.toString();
    }

    protected String getValue(StorageSolutionId id) {
        return id.value();
    }

    protected String getValue(FilePath path) {
        return path.toString();
    }

    protected String getValue(FileCopyFailureReason reason) {
        return reason.value();
    }
}
