package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesReadModelRepository;
import dev.codesoapbox.backity.core.game.application.readmodel.GameWithFileCopiesReadModel;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PageEntityMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PaginationEntityMapper;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameWithFileCopiesReadModelJpaRepository implements GameWithFileCopiesReadModelRepository {

    private static final Sort SORT_BY_GAME_DATE_CREATED_ASC = Sort.by(Sort.Direction.ASC, "dateCreated");
    public static final Pattern TOKENIZER_PATTERN = Pattern.compile("\"(.*?)\"|\\S+");

    private final GameWithFileCopiesReadModelSpringRepository springRepository;
    private final GameWithFilesCopiesReadModelJpaEntityMapper entityMapper;
    private final PageEntityMapper pageMapper;
    private final PaginationEntityMapper paginationMapper;

    @Override
    public Page<GameWithFileCopiesReadModel> findAllPaginated(Pagination pagination, String searchQuery) {
        if (searchQuery != null && searchQuery.isBlank()) {
            searchQuery = null;
        }
        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_GAME_DATE_CREATED_ASC);
        List<String> tokens = tokenize(searchQuery);

        Specification<GameWithFileCopiesReadModelJpaEntity> specification = getSpecificationFetchingGameFiles(tokens);
        org.springframework.data.domain.Page<GameWithFileCopiesReadModelJpaEntity> foundEntities =
                springRepository.findAll(specification, pageable);
        fetchFileCopies(foundEntities);
        return pageMapper.toDomain(foundEntities, entityMapper::toReadModel);
    }

    private void fetchFileCopies(org.springframework.data.domain.Page<GameWithFileCopiesReadModelJpaEntity> foundEntities) {
        List<UUID> gameFileIds = foundEntities.stream()
                .flatMap(e -> e.getGameFilesWithCopies().stream())
                .map(GameFileWithCopiesReadModelJpaEntity::getId)
                .toList();
        springRepository.fetchFileCopies(gameFileIds);
    }

    public List<String> tokenize(String searchQuery) {
        if (searchQuery == null || searchQuery.isBlank()) {
            return emptyList();
        }

        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKENIZER_PATTERN.matcher(searchQuery);

        while (matcher.find()) {
            String quoted = matcher.group(1);
            if (quoted != null) {
                tokens.add(quoted.trim());
            } else {
                tokens.add(matcher.group().trim());
            }
        }

        return tokens;
    }

    @SuppressWarnings("DataFlowIssue") // query is never NULL
    public static Specification<GameWithFileCopiesReadModelJpaEntity> getSpecificationFetchingGameFiles(
            List<String> tokens) {
        return (root, query, cb) -> {
            if (tokens.isEmpty()) {
                return null;
            }
            query.distinct(true);
            Join<?, ?> gameFile = root.join("gameFilesWithCopies", JoinType.LEFT);
            Path<Object> fileSource = gameFile.get("fileSource");
            List<Predicate> tokenPredicates = new ArrayList<>();

            for (String token : tokens) {
                String pattern = "%" + token.toLowerCase() + "%";
                Predicate match = cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(fileSource.get("originalGameTitle")), pattern),
                        cb.like(cb.lower(fileSource.get("fileTitle")), pattern),
                        cb.like(cb.lower(fileSource.get("originalFileName")), pattern)
                );
                tokenPredicates.add(match);
            }

            return cb.or(tokenPredicates.toArray(new Predicate[0]));
        };
    }
}
