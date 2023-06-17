package dev.codesoapbox.backity.core.files.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import org.mapstruct.Mapper;

@Mapper
public abstract class GameFileDetailsMessageMapper {

    public abstract GameFileDetailsMessage toMessage(GameFileDetails domain);

    protected String toString(GameFileDetailsId id) {
        return id.value().toString();
    }

    protected String toString(GameId id) {
        return id.value().toString();
    }
}
