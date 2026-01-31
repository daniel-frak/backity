package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesSearchFilter;
import jakarta.persistence.criteria.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameWithFileCopiesReadModelJpaSpecifications {

    private static final Locale LOCALE = Locale.ENGLISH;
    private static final Pattern TOKENIZER_PATTERN =
            Pattern.compile("\"(.*?)\"|\\S+", Pattern.UNICODE_CHARACTER_CLASS);
    private static final char LIKE_ESCAPE_CHAR = '\\';

    public static Specification<GameWithFileCopiesReadModelJpaEntity> fitsSearchCriteria(
            GameWithFileCopiesSearchFilter filter) {
        String searchQuery = filter.searchQuery();
        return (game, query, builder) -> {
            Join<GameWithFileCopiesReadModelJpaEntity, GameFileWithCopiesReadModelJpaEntity> gameFile =
                    game.join(GameWithFileCopiesReadModelJpaEntity_.gameFilesWithCopies, JoinType.LEFT);

            // Prevent duplicate Games when a Game has multiple matching GameFiles.
            // Also helps count query produce correct totals in most providers.
            Objects.requireNonNull(query).distinct(true);

            return builder.and(
                    buildFileCopyStatusPredicate(filter, game, query, builder),
                    buildSearchQueryPredicate(searchQuery, game, gameFile, query, builder)
            );
        };
    }

    private static Predicate buildFileCopyStatusPredicate(
            GameWithFileCopiesSearchFilter filter,
            Root<GameWithFileCopiesReadModelJpaEntity> game,
            CriteriaQuery<?> query,
            CriteriaBuilder builder) {

        if (filter.status() == null) {
            return builder.conjunction();
        }

        // Use EXISTS instead of joining fileCopies in the main query to avoid row multiplication
        // (which breaks pagination totals).
        Subquery<Integer> existsSubquery = query.subquery(Integer.class);
        Root<GameFileWithCopiesReadModelJpaEntity> gameFile =
                existsSubquery.from(GameFileWithCopiesReadModelJpaEntity.class);
        ListJoin<GameFileWithCopiesReadModelJpaEntity, FileCopyReadModelJpaEntity> fileCopy =
                gameFile.join(GameFileWithCopiesReadModelJpaEntity_.fileCopies, JoinType.INNER);

        existsSubquery.select(builder.literal(1));
        existsSubquery.where(
                builder.equal(gameFile.get(GameFileWithCopiesReadModelJpaEntity_.gameId),
                        game.get(GameWithFileCopiesReadModelJpaEntity_.id)),
                builder.equal(fileCopy.get(FileCopyReadModelJpaEntity_.status), filter.status())
        );

        return builder.exists(existsSubquery);
    }

    private static Predicate buildSearchQueryPredicate(
            String searchQuery, Root<GameWithFileCopiesReadModelJpaEntity> game,
            Join<GameWithFileCopiesReadModelJpaEntity, GameFileWithCopiesReadModelJpaEntity> gameFile,
            CriteriaQuery<?> query,
            CriteriaBuilder builder) {
        if (searchQuery == null || searchQuery.isBlank()) {
            return builder.conjunction();
        }
        query.distinct(true);

        Path<Object> fileSource = gameFile.get(GameFileWithCopiesReadModelJpaEntity_.FILE_SOURCE);

        String escapedSearchQuery = escapeForLikePattern(searchQuery);
        List<Predicate> tokenPredicates =
                buildSearchQueryTokenPredicates(game, builder, escapedSearchQuery, fileSource);

        return builder.and(tokenPredicates.toArray(new Predicate[0]));
    }

    private static String escapeForLikePattern(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    private static List<Predicate> buildSearchQueryTokenPredicates(
            Root<GameWithFileCopiesReadModelJpaEntity> root, CriteriaBuilder builder, String searchQuery,
            Path<Object> fileSource) {
        List<String> searchQueryTokens = tokenize(searchQuery);

        List<Predicate> tokenPredicates = new ArrayList<>();
        Expression<String> titleLower = builder.lower(root.get(GameWithFileCopiesReadModelJpaEntity_.title));
        Expression<String> originalGameTitleLower =
                builder.lower(fileSource.get(FileSourceReadModelJpaEmbeddable_.ORIGINAL_GAME_TITLE));
        Expression<String> fileTitleLower =
                builder.lower(fileSource.get(FileSourceReadModelJpaEmbeddable_.FILE_TITLE));
        Expression<String> originalFileNameLower =
                builder.lower(fileSource.get(FileSourceReadModelJpaEmbeddable_.ORIGINAL_FILE_NAME));
        for (String token : searchQueryTokens) {
            String pattern = "%" + token.toLowerCase(LOCALE) + "%";
            tokenPredicates.add(builder.or(
                    builder.like(titleLower, pattern, LIKE_ESCAPE_CHAR),
                    builder.like(originalGameTitleLower, pattern, LIKE_ESCAPE_CHAR),
                    builder.like(fileTitleLower, pattern, LIKE_ESCAPE_CHAR),
                    builder.like(originalFileNameLower, pattern, LIKE_ESCAPE_CHAR)
            ));
        }

        return tokenPredicates;
    }


    private static List<String> tokenize(String searchQuery) {
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
}
