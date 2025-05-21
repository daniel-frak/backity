package dev.codesoapbox.backity.core.storagesolution.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class StringSanitizer {

    // Using specifically HashSet::contains for illegal character removal is much faster than, e.g., using regex
    private final HashSet<Character> charactersToRemove;
    private final List<StringReplacement> charactersToReplace;

    public StringSanitizer(Set<Character> charactersToRemove, List<StringReplacement> charactersToReplace) {
        this.charactersToRemove = new HashSet<>(charactersToRemove);
        this.charactersToReplace = charactersToReplace;
    }

    public String sanitize(String value) {
        String sanitized = removeIllegalCharacters(value);
        for (StringReplacement stringReplacement : charactersToReplace) {
            sanitized = sanitized.replace(stringReplacement.from(), stringReplacement.to());
        }
        return sanitized;
    }

    private String removeIllegalCharacters(String value) {
        var sanitized = new StringBuilder(value.length());
        for (char c : value.toCharArray()) {
            if (!charactersToRemove.contains(c)) {
                sanitized.append(c);
            }
        }
        return sanitized.toString();
    }

    public StringSanitizer withAdditionalCharactersToRemove(
            Set<Character> additionalCharactersToRemove) {
        HashSet<Character> mergedCharactersToRemove = new HashSet<>(this.charactersToRemove);
        mergedCharactersToRemove.addAll(additionalCharactersToRemove);

        return new StringSanitizer(mergedCharactersToRemove, this.charactersToReplace);
    }

    public record StringReplacement(
            String from,
            String to
    ) {
    }
}
