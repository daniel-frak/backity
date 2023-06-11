package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = GameIdJsonMapper.class)
public abstract class GameFileDetailsJsonMapper {

    public static final GameFileDetailsJsonMapper INSTANCE = Mappers.getMapper(GameFileDetailsJsonMapper.class);

    public abstract GameFileDetailsJson toJson(GameFileDetails domain);

    protected String toString(GameFileDetailsId id) {
        return id.value().toString();
    }
}
