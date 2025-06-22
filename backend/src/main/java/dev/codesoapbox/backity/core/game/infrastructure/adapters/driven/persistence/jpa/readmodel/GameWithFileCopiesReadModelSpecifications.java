package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import jakarta.persistence.criteria.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameWithFileCopiesReadModelSpecifications {

    private static final Locale LOCALE = Locale.ENGLISH;
    private static final Pattern TOKENIZER_PATTERN =
            Pattern.compile("\"(.*?)\"|\\S+", Pattern.UNICODE_CHARACTER_CLASS);
    public static final char LIKE_ESCAPE_CHAR = '\\';

    @SuppressWarnings("DataFlowIssue") // searchQuery is never NULL
    public static Specification<GameWithFileCopiesReadModelJpaEntity> fitsSearchCriteria(String searchQuery) {
        return (root, query, builder) -> {
            if (searchQuery == null || searchQuery.isBlank()) {
                return null;
            }

            // Don't add distinct for count query
            return buildSearchPredicate(searchQuery, root, query, builder);
        };
    }

    private static Predicate buildSearchPredicate(
            String searchQuery, Root<GameWithFileCopiesReadModelJpaEntity> game, CriteriaQuery<?> query,
            CriteriaBuilder builder) {
        query.distinct(true);

        Join<?, ?> gameFile = game.join(GameWithFileCopiesReadModelJpaEntity_.gameFilesWithCopies, JoinType.LEFT);
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
