package dev.codesoapbox.backity.core.storagesolution.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class UniqueFilePathResolver {

    private static final HashSet<Character> CHARACTERS_TO_REMOVE_FROM_TEMPLATE = new HashSet<>(Set.of(
            '<', '>', '"', '|', '?', '\n', '`', ';', '!', '@', '#', '$', '%', '^', '&', '*', '[', ']', '~', '\''
    ));
    private static final List<CharacterReplacement> CHARACTERS_TO_REPLACE = List.of(
            new CharacterReplacement(":", " -"),
            new CharacterReplacement("\t", " ")
    );
    private static final StringSanitizer PATH_TEMPLATE_SANITIZER = new StringSanitizer(
            CHARACTERS_TO_REMOVE_FROM_TEMPLATE, CHARACTERS_TO_REPLACE);
    private static final StringSanitizer PLACEHOLDER_SANITIZER = PATH_TEMPLATE_SANITIZER
            .withAdditionalCharactersToRemove(Set.of('{', '}'));

    final String defaultPathTemplate;
    private final StorageSolution storageSolution;

    public UniqueFilePathResolver(String defaultPathTemplate, StorageSolution storageSolution) {
        this.storageSolution = storageSolution;
        this.defaultPathTemplate = PATH_TEMPLATE_SANITIZER.sanitize(
                replaceWithCorrectFileSeparator(defaultPathTemplate));
    }

    private String replaceWithCorrectFileSeparator(String defaultPathTemplate) {
        return defaultPathTemplate
                .replace("/", storageSolution.getSeparator())
                .replace("\\", storageSolution.getSeparator());
    }

    public String resolve(FileSource fileSource) {
        RawPathData rawPathData = RawPathData.from(fileSource);
        return constructPathUntilUnique(rawPathData);
    }

    private String constructPathUntilUnique(RawPathData rawPathData) {
        int suffixIndex = 0;
        String filePath;
        do {
            filePath = constructPath(rawPathData, suffixIndex++);
        } while (storageSolution.fileExists(filePath));

        return filePath;
    }

    private String constructPath(RawPathData rawPathData, int suffixIndex) {
        String targetFileName = constructFileName(rawPathData, suffixIndex);

        return defaultPathTemplate
                .replace("{GAME_PROVIDER_ID}",
                        PLACEHOLDER_SANITIZER.sanitize(rawPathData.gameProviderId().value()))
                .replace("{TITLE}", PLACEHOLDER_SANITIZER.sanitize(rawPathData.gameTitle()))
                .replace("{FILENAME}", PLACEHOLDER_SANITIZER.sanitize(targetFileName));
    }

    private String constructFileName(RawPathData rawPathData, int suffixIndex) {
        String targetBaseName = (suffixIndex > 0) ? rawPathData.baseName() + "_" + suffixIndex : rawPathData.baseName();
        return targetBaseName + rawPathData.extensionWithDot();
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class StringSanitizer {

        // Using specifically HashSet::contains for illegal character removal is much faster than, e.g., using regex
        private final HashSet<Character> charactersToRemove;
        private final List<CharacterReplacement> charactersToReplace;

        public String sanitize(String value) {
            String sanitized = removeIllegalCharacters(value);
            for (CharacterReplacement characterReplacement : charactersToReplace) {
                sanitized = sanitized.replace(characterReplacement.from(), characterReplacement.to());
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
    }

    private record CharacterReplacement(
            String from,
            String to
    ) {
    }

    private record RawPathData(
            GameProviderId gameProviderId,
            String gameTitle,
            String fileName,
            String baseName,
            String extensionWithDot
    ) {

        public static RawPathData from(FileSource fileSource) {
            String fileName = fileSource.originalFileName();
            String baseName = FilenameUtils.getBaseName(fileName);
            String extensionWithDot = fileName.substring(baseName.length());

            return new RawPathData(
                    fileSource.gameProviderId(),
                    fileSource.originalGameTitle(),
                    fileName,
                    baseName,
                    extensionWithDot
            );
        }
    }
}
