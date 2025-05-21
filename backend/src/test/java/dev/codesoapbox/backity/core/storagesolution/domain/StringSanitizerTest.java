package dev.codesoapbox.backity.core.storagesolution.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

class StringSanitizerTest {

    @Nested
    class Creation {

        @Test
        void shouldConstructWithAdditionalCharactersToRemove() {
            Set<Character> charactersToRemove = Set.of('$');
            List<StringSanitizer.StringReplacement> charactersToReplace =
                    List.of(new StringSanitizer.StringReplacement("-", "_"));
            var originalSanitizer = new StringSanitizer(charactersToRemove, charactersToReplace);
            Set<Character> additionalCharactersToRemove = Set.of('!');

            StringSanitizer newSanitizer =
                    originalSanitizer.withAdditionalCharactersToRemove(additionalCharactersToRemove);

            String sanitized = newSanitizer.sanitize("$!$!");
            assertThat(sanitized).isEmpty();
        }
    }

    @Nested
    class Sanitization {

        @Test
        void sanitizeShouldRemoveCharacters() {
            Set<Character> charactersToRemove = Set.of('$');
            var sanitizer = new StringSanitizer(charactersToRemove, emptyList());

            String result = sanitizer.sanitize("Te$t$");

            assertThat(result).isEqualTo("Tet");
        }

        @Test
        void sanitizeShouldReplaceStrings() {
            List<StringSanitizer.StringReplacement> charactersToReplace =
                    List.of(new StringSanitizer.StringReplacement("-", "_"));
            var sanitizer = new StringSanitizer(emptySet(), charactersToReplace);

            String result = sanitizer.sanitize("Test-1");

            assertThat(result).isEqualTo("Test_1");
        }
    }
}