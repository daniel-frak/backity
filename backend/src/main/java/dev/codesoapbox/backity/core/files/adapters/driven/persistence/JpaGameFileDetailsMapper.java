package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import org.mapstruct.Mapper;

@Mapper
public abstract class JpaGameFileDetailsMapper {

    public abstract JpaGameFileDetails toEntity(GameFileDetails model);

    public abstract GameFileDetails toModel(JpaGameFileDetails entity);
}
