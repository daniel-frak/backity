package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SharedJpaDtoMapperConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

@Mapper(config = SharedJpaDtoMapperConfig.class,
        uses = {
                GameValueObjectJpaDtoMapper.class
        })
public interface GameJpaEntityMapper {

    GameJpaEntity toEntity(Game model);

    @BeanMapping(ignoreUnmappedSourceProperties = {"dateCreated", "dateModified"})
    Game toDomain(GameJpaEntity entity);
}
