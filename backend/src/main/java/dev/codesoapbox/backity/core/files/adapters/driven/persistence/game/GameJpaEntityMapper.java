package dev.codesoapbox.backity.core.files.adapters.driven.persistence.game;

import dev.codesoapbox.backity.core.files.domain.game.Game;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.UUID;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class GameJpaEntityMapper {

    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "dateModified", ignore = true)
    public abstract GameJpaEntity toEntity(Game model);

    protected UUID toUuid(GameId id) {
        return id.value();
    }

    @BeanMapping(ignoreUnmappedSourceProperties = {"dateCreated", "dateModified"})
    public abstract Game toDomain(GameJpaEntity entity);

    protected GameId toId(UUID uuid) {
        return new GameId(uuid);
    }
}
