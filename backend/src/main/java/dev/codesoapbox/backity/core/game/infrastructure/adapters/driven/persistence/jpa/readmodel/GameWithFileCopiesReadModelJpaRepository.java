package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesReadModelRepository;
import dev.codesoapbox.backity.core.game.application.readmodel.GameWithFileCopiesReadModel;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PageEntityMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PaginationEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameWithFileCopiesReadModelJpaRepository implements GameWithFileCopiesReadModelRepository {

    private static final Sort SORT_BY_GAME_DATE_CREATED_ASC = Sort.by(Sort.Direction.ASC, "dateCreated");

    private final GameWithFileCopiesReadModelSpringRepository springRepository;
    private final GameWithFilesCopiesReadModelJpaEntityMapper entityMapper;
    private final PageEntityMapper pageMapper;
    private final PaginationEntityMapper paginationMapper;

    @Override
    public Page<GameWithFileCopiesReadModel> findAll(Pagination pagination) {
        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_GAME_DATE_CREATED_ASC);
        org.springframework.data.domain.Page<GameWithFileCopiesReadModelJpaEntity> foundEntities =
                springRepository.findAll(pageable);
        return pageMapper.toDomain(foundEntities, entityMapper::toReadModel);
    }
}
