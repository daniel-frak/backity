package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.game.domain.exceptions.GameNotFoundException;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PageEntityMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PaginationEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameJpaRepository implements GameRepository {

    private static final Sort SORT_BY_DATE_CREATED_ASC = Sort.by(Sort.Direction.ASC, "dateCreated");

    private final GameJpaEntitySpringRepository springRepository;
    private final GameJpaEntityMapper entityMapper;
    private final PageEntityMapper pageMapper;
    private final PaginationEntityMapper paginationMapper;

    @Transactional
    @Override
    public Game save(Game game) {
        GameJpaEntity entity = entityMapper.toEntity(game);

        GameJpaEntity savedEntity = springRepository.save(entity);
        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Game> findByTitle(String title) {
        return springRepository.findByTitle(title)
                .map(entityMapper::toDomain);
    }

    @Override
    public Game getById(GameId gameId) {
        return findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }

    @Override
    public Optional<Game> findById(GameId id) {
        return springRepository.findById(id.value())
                .map(entityMapper::toDomain);
    }

    @Override
    public Page<Game> findAll(Pagination pagination) {
        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_DATE_CREATED_ASC);
        org.springframework.data.domain.Page<GameJpaEntity> foundEntities = springRepository.findAll(pageable);
        return pageMapper.toDomain(foundEntities, entityMapper::toDomain);
    }

    @Override
    public List<Game> findAllByIdIn(Collection<GameId> ids) {
        List<UUID> gameUuids = ids.stream()
                .map(GameId::value)
                .toList();
        return springRepository.findAllByIdIn(gameUuids).stream()
                .map(entityMapper::toDomain)
                .toList();
    }
}
