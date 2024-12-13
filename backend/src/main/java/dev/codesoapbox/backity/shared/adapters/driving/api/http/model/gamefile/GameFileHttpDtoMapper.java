package dev.codesoapbox.backity.shared.adapters.driving.api.http.model.gamefile;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.adapters.driving.api.http.model.game.GameIdHttpDtoMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

@Mapper(uses = GameIdHttpDtoMapper.class)
public abstract class GameFileHttpDtoMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = "domainEvents")
    public abstract GameFileHttpDto toDto(GameFile domain);

    protected String toString(GameFileId id) {
        return id.value().toString();
    }

    protected String toString(GameProviderId gameProviderId) {
        return gameProviderId.value();
    }
}