package dev.codesoapbox.backity.core.gamefile.config;

import dev.codesoapbox.backity.core.gamefile.adapters.driven.persistence.jpa.GameFileJpaEntityMapper;
import dev.codesoapbox.backity.core.gamefile.adapters.driven.persistence.jpa.GameFileJpaEntitySpringRepository;
import dev.codesoapbox.backity.core.gamefile.adapters.driven.persistence.jpa.GameFileJpaRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.shared.adapters.driven.persistence.PageEntityMapper;
import dev.codesoapbox.backity.core.shared.adapters.driven.persistence.PaginationEntityMapper;
import dev.codesoapbox.backity.core.shared.domain.DomainEventPublisher;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameFileJpaRepositoryBeanConfig {

    @Bean
    GameFileJpaEntityMapper gameFileJpaEntityMapper() {
        return Mappers.getMapper(GameFileJpaEntityMapper.class);
    }

    @Bean
    GameFileRepository gameFileRepository(GameFileJpaEntitySpringRepository springRepository,
                                          GameFileJpaEntityMapper entityMapper,
                                          PageEntityMapper pageMapper,
                                          PaginationEntityMapper paginationMapper,
                                          DomainEventPublisher domainEventPublisher) {
        return new GameFileJpaRepository(springRepository, entityMapper, pageMapper, paginationMapper,
                domainEventPublisher);
    }
}
