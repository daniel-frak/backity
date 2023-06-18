package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefiledetails;

import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsId;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.game.GameIdHttpDtoMapper;
import org.mapstruct.Mapper;

@Mapper(uses = GameIdHttpDtoMapper.class)
public abstract class GameFileDetailsHttpDtoMapper {

    public abstract GameFileDetailsHttpDto toDto(GameFileDetails domain);

    protected String toString(GameFileDetailsId id) {
        return id.value().toString();
    }
}
