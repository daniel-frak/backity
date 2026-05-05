package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyFailureReason;
import dev.codesoapbox.backity.core.game.application.readmodel.FileCopyReadModel;
import dev.codesoapbox.backity.core.game.application.readmodel.GameWithFileCopiesReadModel;
import dev.codesoapbox.backity.core.game.application.readmodel.SourceFileReadModel;
import dev.codesoapbox.backity.core.game.application.readmodel.SourceFileWithCopiesReadModel;
import dev.codesoapbox.backity.core.sourcefile.domain.FileSize;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;
import org.mapstruct.*;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class GameWithFilesCopiesReadModelJpaEntityMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = {"dateCreated", "dateModified"})
    public abstract GameWithFileCopiesReadModel toReadModel(GameWithFileCopiesReadModelJpaEntity entity);

    @Mapping(target = "sourceFile", source = ".")
    @Mapping(target = "fileCopies", source = "fileCopies")
    @BeanMapping(unmappedSourcePolicy = ReportingPolicy.IGNORE)
    protected abstract SourceFileWithCopiesReadModel toReadModel(SourceFileWithCopiesReadModelJpaEntity entity);

    @Mapping(target = "size", source = "sizeInBytes", qualifiedByName = "mapFileSize")
    @BeanMapping(unmappedSourcePolicy = ReportingPolicy.IGNORE)
    protected abstract SourceFileReadModel toReadModelInternal(SourceFileWithCopiesReadModelJpaEntity entity);

    protected abstract FileCopyReadModel toReadModel(FileCopyReadModelJpaEntity entity);

    protected FilePath toFilePath(String value) {
        return new FilePath(value);
    }

    protected FileCopyFailureReason toFileCopyFailureReason(String reason) {
        return new FileCopyFailureReason(reason);
    }

    @Named("mapFileSize")
    protected String mapFileSize(Long sizeInBytes) {
        return new FileSize(sizeInBytes).toString();
    }
}
