package dev.codesoapbox.backity.core.files.adapters.driven.persistence.game;

import dev.codesoapbox.backity.core.files.domain.game.Game;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import dev.codesoapbox.backity.core.files.domain.game.GameRepository;
import dev.codesoapbox.backity.core.shared.adapters.driven.persistence.PageEntityMapper;
import dev.codesoapbox.backity.core.shared.adapters.driven.persistence.PaginationEntityMapper;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
public class GameJpaRepository implements GameRepository {

    private static final Sort SORT_BY_DATE_CREATED_ASC = Sort.by(Sort.Direction.ASC, "dateCreated");

    private final GameJpaEntitySpringRepository springRepository;
    private final GameJpaEntityMapper entityMapper;
    private final PageEntityMapper pageMapper;
    private final PaginationEntityMapper paginationMapper;

    @Transactional
    @Override
    public void save(Game game) {
        var entity = entityMapper.toEntity(game);

        springRepository.save(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Game> findByTitle(String title) {
        return springRepository.findByTitle(title)
                .map(entityMapper::toDomain);
    }

    @Transactional(readOnly = true)
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
}
