package dev.codesoapbox.backity.core.files.adapters.driven.persistence.game;

import dev.codesoapbox.backity.core.files.domain.game.Game;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

import java.util.UUID;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class JpaGameMapper {

    public abstract JpaGame toEntity(Game model);

    protected UUID toUuid(GameId id) {
        return id.value();
    }
}
