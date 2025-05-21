package dev.codesoapbox.backity.core.storagesolution.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import dev.codesoapbox.backity.core.storagesolution.domain.exceptions.CouldNotResolveUniqueFilePathException;
import lombok.NonNull;
import org.apache.commons.io.FilenameUtils;

import java.util.List;
import java.util.Set;

public class UniqueFilePathResolver {

    private static final int MAX_ATTEMPTS = 1000;
    private static final Set<Character> CHARACTERS_TO_REMOVE_FROM_TEMPLATE = Set.of(
            '<', '>', '"', '|', '?', '\n', '`', ';', '!', '@', '#', '$', '%', '^', '&', '*', '[', ']', '~', '\''
    );
    private static final List<StringSanitizer.StringReplacement> CHARACTERS_TO_REPLACE = List.of(
            new StringSanitizer.StringReplacement(":", " -"),
            new StringSanitizer.StringReplacement("\t", " ")
    );
    private static final StringSanitizer PATH_TEMPLATE_SANITIZER = new StringSanitizer(
            CHARACTERS_TO_REMOVE_FROM_TEMPLATE, CHARACTERS_TO_REPLACE);
    private static final StringSanitizer PLACEHOLDER_SANITIZER = PATH_TEMPLATE_SANITIZER
            .withAdditionalCharactersToRemove(Set.of('{', '}'));

    private final String defaultPathTemplate;
    private final StorageSolution storageSolution;

    public UniqueFilePathResolver(@NonNull String defaultPathTemplate, @NonNull StorageSolution storageSolution) {
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
        int attemptNumber = 0;
        do {
            attemptNumber++;
            if (attemptNumber >= MAX_ATTEMPTS) {
                throw new CouldNotResolveUniqueFilePathException(rawPathData.gameTitle(),
                        rawPathData.baseName() + rawPathData.extensionWithDot, attemptNumber);
            }

            filePath = constructPath(rawPathData, suffixIndex);
            suffixIndex++;
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
        String targetBaseName =
                (suffixIndex > 0) ? (rawPathData.baseName() + "_" + suffixIndex) : rawPathData.baseName();
        return targetBaseName + rawPathData.extensionWithDot();
    }

    private record RawPathData(
            GameProviderId gameProviderId,
            String gameTitle,
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
                    baseName,
                    extensionWithDot
            );
        }
    }
}
