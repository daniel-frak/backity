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

import static java.util.Collections.emptyList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameWithFileCopiesReadModelSpecifications {

    private static final Locale LOCALE = Locale.ENGLISH;
    private static final Pattern TOKENIZER_PATTERN =
            Pattern.compile("\"(.*?)\"|\\S+", Pattern.UNICODE_CHARACTER_CLASS);

    @SuppressWarnings("DataFlowIssue") // Query is never NULL
    public static Specification<GameWithFileCopiesReadModelJpaEntity> fitsSearchCriteria(String searchQuery) {
        return (root, query, builder) -> {
            if (searchQuery == null || searchQuery.isEmpty()) {
                return null;
            }

            // Don't add distinct for count query
            if (query.getResultType() != Long.class) {
                query.distinct(true);
            }

            Join<?, ?> gameFile = root.join("gameFilesWithCopies", JoinType.LEFT);
            Path<Object> fileSource = gameFile.get("fileSource");

            List<Predicate> tokenPredicates = buildSearchQueryTokenPredicates(root, builder, searchQuery, fileSource);

            return builder.or(tokenPredicates.toArray(new Predicate[0]));
        };
    }

    private static List<Predicate> buildSearchQueryTokenPredicates(
            Root<GameWithFileCopiesReadModelJpaEntity> root, CriteriaBuilder builder, String searchQuery,
            Path<Object> fileSource) {
        List<String> searchQueryTokens = tokenize(searchQuery);

        List<Predicate> tokenPredicates = new ArrayList<>();
        for (String token : searchQueryTokens) {
            String pattern = "%" + token.toLowerCase(LOCALE) + "%";
            tokenPredicates.add(builder.or(
                    builder.like(builder.lower(root.get("title")), pattern),
                    builder.like(builder.lower(fileSource.get("originalGameTitle")), pattern),
                    builder.like(builder.lower(fileSource.get("fileTitle")), pattern),
                    builder.like(builder.lower(fileSource.get("originalFileName")), pattern)
            ));
        }

        return tokenPredicates;
    }


    private static List<String> tokenize(String searchQuery) {
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
}
