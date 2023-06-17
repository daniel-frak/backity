package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = GameIdHttpDtoMapper.class)
public abstract class GameFileDetailsHttpDtoMapper {

    public static final GameFileDetailsHttpDtoMapper INSTANCE = Mappers.getMapper(GameFileDetailsHttpDtoMapper.class);

    public abstract GameFileDetailsHttpDto toDto(GameFileDetails domain);

    protected String toString(GameFileDetailsId id) {
        return id.value().toString();
    }
}
