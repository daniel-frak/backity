package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesReadModelRepository;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesSearchFilter;
import dev.codesoapbox.backity.core.game.application.readmodel.GameWithFileCopiesReadModel;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageableMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameWithFileCopiesReadModelJpaRepository implements GameWithFileCopiesReadModelRepository {

    private static final Sort SORT_BY_GAME_DATE_CREATED_DESC = Sort.by(Sort.Direction.DESC, "dateCreated");

    private final GameWithFileCopiesReadModelSpringRepository springRepository;
    private final GameWithFilesCopiesReadModelJpaEntityMapper entityMapper;
    private final SpringPageMapper pageMapper;
    private final SpringPageableMapper paginationMapper;

    @Override
    public Page<GameWithFileCopiesReadModel> findAllPaginated(Pagination pagination,
                                                              GameWithFileCopiesSearchFilter filter) {
        Pageable pageable = paginationMapper.toPageable(pagination, SORT_BY_GAME_DATE_CREATED_DESC);

        Specification<GameWithFileCopiesReadModelJpaEntity> specification =
                GameWithFileCopiesReadModelJpaSpecifications.fitsSearchCriteria(filter);
        org.springframework.data.domain.Page<GameWithFileCopiesReadModelJpaEntity> foundEntities =
                springRepository.findAll(specification, pageable);
        fetchFileCopies(foundEntities);
        return pageMapper.toDomain(foundEntities, entityMapper::toReadModel);
    }

    private void fetchFileCopies(
            org.springframework.data.domain.Page<GameWithFileCopiesReadModelJpaEntity> foundEntities) {
        List<UUID> gameFileIds = foundEntities.stream()
                .flatMap(e -> e.getGameFilesWithCopies().stream())
                .map(GameFileWithCopiesReadModelJpaEntity::getId)
                .toList();
        springRepository.fetchFileCopies(gameFileIds);
    }
}
