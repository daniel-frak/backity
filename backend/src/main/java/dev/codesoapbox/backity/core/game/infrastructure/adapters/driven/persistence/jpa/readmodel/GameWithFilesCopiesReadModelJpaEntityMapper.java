package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import dev.codesoapbox.backity.core.game.application.readmodel.*;
import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import org.mapstruct.*;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class GameWithFilesCopiesReadModelJpaEntityMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = {"dateCreated", "dateModified"})
    public abstract GameWithFileCopiesReadModel toReadModel(GameWithFileCopiesReadModelJpaEntity entity);

    @Mapping(target = "gameFile", source = ".")
    @Mapping(target = "fileCopies", source = "fileCopies")
    @BeanMapping(unmappedSourcePolicy = ReportingPolicy.IGNORE)
    protected abstract GameFileWithCopiesReadModel toReadModel(GameFileWithCopiesReadModelJpaEntity entity);

    @BeanMapping(unmappedSourcePolicy = ReportingPolicy.IGNORE)
    protected abstract GameFileReadModel toReadModelInternal(GameFileWithCopiesReadModelJpaEntity entity);

    protected abstract FileCopyReadModel toReadModel(FileCopyReadModelJpaEntity entity);

    @Mapping(target = "size", source = "sizeInBytes", qualifiedByName = "mapFileSize")
    @BeanMapping(ignoreUnmappedSourceProperties = "sizeInBytes")
    protected abstract FileSourceReadModel toReadModel(FileSourceReadModelJpaEmbeddable entity);

    @Named("mapFileSize")
    protected String mapFileSize(Long sizeInBytes) {
        return new FileSize(sizeInBytes).toString();
    }
}
