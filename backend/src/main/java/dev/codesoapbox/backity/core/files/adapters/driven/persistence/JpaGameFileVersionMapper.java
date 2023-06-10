package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersion;
import org.mapstruct.Mapper;

@Mapper
public abstract class JpaGameFileVersionMapper {

    public abstract JpaGameFileVersion toEntity(GameFileVersion model);

    public abstract GameFileVersion toModel(JpaGameFileVersion entity);
}
