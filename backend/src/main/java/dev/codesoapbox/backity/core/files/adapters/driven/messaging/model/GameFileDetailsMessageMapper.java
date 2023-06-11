package dev.codesoapbox.backity.core.files.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class GameFileDetailsMessageMapper {

    public static final GameFileDetailsMessageMapper INSTANCE = Mappers.getMapper(GameFileDetailsMessageMapper.class);

    public abstract GameFileDetailsMessage toMessage(GameFileDetails domain);

    protected String toString(GameFileDetailsId id) {
        return id.value().toString();
    }

    protected String toString(GameId id) {
        return id.value().toString();
    }
}
