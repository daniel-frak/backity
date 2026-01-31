package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
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

        return hasAnyFileCopyWithStatus(filter.status())
                .and(matchesSearchQuery(filter.searchQuery()));
    }

    private static Specification<GameWithFileCopiesReadModelJpaEntity> hasAnyFileCopyWithStatus(FileCopyStatus status) {
        if (status == null) {
            return Specification.unrestricted();
        }

        return (game, query, builder) -> {
            // Filter via EXISTS to avoid row multiplication (keeps pagination totals correct).
            Subquery<Integer> subquery = Objects.requireNonNull(query).subquery(Integer.class);
            Root<GameFileWithCopiesReadModelJpaEntity> gameFile =
                    subquery.from(GameFileWithCopiesReadModelJpaEntity.class);
            ListJoin<GameFileWithCopiesReadModelJpaEntity, FileCopyReadModelJpaEntity> fileCopy =
                    gameFile.join(GameFileWithCopiesReadModelJpaEntity_.fileCopies, JoinType.INNER);

            subquery.select(builder.literal(1))
                    .where(
                            builder.equal(gameFile.get(GameFileWithCopiesReadModelJpaEntity_.gameId),
                                    game.get(GameWithFileCopiesReadModelJpaEntity_.id)),
                            builder.equal(fileCopy.get(FileCopyReadModelJpaEntity_.status), status)
                    );

            return builder.exists(subquery);
        };
    }

    private static Specification<GameWithFileCopiesReadModelJpaEntity> matchesSearchQuery(String searchQuery) {
        if (searchQuery == null || searchQuery.isBlank()) {
            return Specification.unrestricted();
        }

        List<String> tokens = tokenize(searchQuery);
        if (tokens.isEmpty()) {
            return Specification.unrestricted();
        }

        return (game, query, builder) -> {
            Objects.requireNonNull(query);

            List<String> likePatternsLower = toLikePatternsLower(tokens);

            Expression<String> gameTitleLower = builder.lower(game.get(GameWithFileCopiesReadModelJpaEntity_.title));
            Predicate matchesAnyTokenInTitle = anyLike(builder, gameTitleLower, likePatternsLower);

            // Match ANY token in ANY of file source fields using EXISTS (pagination-safe).
            Subquery<Integer> fileMatchSubquery = query.subquery(Integer.class);
            Root<GameFileWithCopiesReadModelJpaEntity> gameFile =
                    fileMatchSubquery.from(GameFileWithCopiesReadModelJpaEntity.class);

            Path<Object> fileSource = gameFile.get(GameFileWithCopiesReadModelJpaEntity_.FILE_SOURCE);

            Expression<String> originalGameTitleLower =
                    builder.lower(fileSource.get(FileSourceReadModelJpaEmbeddable_.ORIGINAL_GAME_TITLE));
            Expression<String> fileTitleLower =
                    builder.lower(fileSource.get(FileSourceReadModelJpaEmbeddable_.FILE_TITLE));
            Expression<String> originalFileNameLower =
                    builder.lower(fileSource.get(FileSourceReadModelJpaEmbeddable_.ORIGINAL_FILE_NAME));

            Predicate matchesAnyTokenInAnyFileSourceField = builder.or(
                    anyLike(builder, originalGameTitleLower, likePatternsLower),
                    anyLike(builder, fileTitleLower, likePatternsLower),
                    anyLike(builder, originalFileNameLower, likePatternsLower)
            );

            fileMatchSubquery.select(builder.literal(1))
                    .where(
                            builder.equal(
                                    gameFile.get(GameFileWithCopiesReadModelJpaEntity_.gameId),
                                    game.get(GameWithFileCopiesReadModelJpaEntity_.id)
                            ),
                            matchesAnyTokenInAnyFileSourceField
                    );

            return builder.or(matchesAnyTokenInTitle, builder.exists(fileMatchSubquery));
        };
    }

    private static Predicate anyLike(
            CriteriaBuilder builder,
            Expression<String> valueLower,
            List<String> likePatternsLower
    ) {
        List<Predicate> predicates = new ArrayList<>(likePatternsLower.size());
        for (String patternLower : likePatternsLower) {
            predicates.add(builder.like(valueLower, patternLower, LIKE_ESCAPE_CHAR));
        }
        return builder.or(predicates.toArray(new Predicate[0]));
    }

    private static List<String> toLikePatternsLower(List<String> tokens) {
        List<String> likePatternsLower = new ArrayList<>(tokens.size());
        for (String token : tokens) {
            String escapedTokenLower = escapeForLikePattern(token).toLowerCase(LOCALE);
            likePatternsLower.add("%" + escapedTokenLower + "%");
        }
        return likePatternsLower;
    }

    private static String escapeForLikePattern(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    private static List<String> tokenize(String searchQuery) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKENIZER_PATTERN.matcher(searchQuery);

        while (matcher.find()) {
            String quoted = matcher.group(1);
            String token = (quoted != null)
                    ? quoted.trim()
                    : matcher.group().trim();

            // Avoid empty tokens (e.g. "" or "   "), which can produce "%%" and match everything.
            if (!token.isBlank()) {
                tokens.add(token);
            }
        }

        return tokens;
    }
}
