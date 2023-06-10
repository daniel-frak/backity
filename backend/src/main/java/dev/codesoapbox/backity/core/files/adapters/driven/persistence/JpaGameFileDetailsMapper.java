package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper
public abstract class JpaGameFileDetailsMapper {

    public abstract JpaGameFileDetails toEntity(GameFileDetails model);

    public abstract GameFileDetails toModel(JpaGameFileDetails entity);

    protected UUID toUuid(GameFileDetailsId id) {
        return id.value();
    }

    protected GameFileDetailsId toModel(UUID uuid) {
        return new GameFileDetailsId(uuid);
    }
}
