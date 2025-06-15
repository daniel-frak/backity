package dev.codesoapbox.backity.core.storagesolution.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import dev.codesoapbox.backity.core.storagesolution.domain.exceptions.CouldNotResolveUniqueFilePathException;

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


    private String replaceWithCorrectFileSeparator(String pathTemplate, StorageSolution storageSolution) {
        return pathTemplate
                .replace("/", storageSolution.getSeparator())
                .replace("\\", storageSolution.getSeparator());
    }

    public String resolve(String pathTemplate, FileSource fileSource, StorageSolution storageSolution) {
        String sanitizedPathTemplate = PATH_TEMPLATE_SANITIZER.sanitize(
                replaceWithCorrectFileSeparator(pathTemplate, storageSolution));
        RawPathData rawPathData = RawPathData.from(fileSource);
        return constructPathUntilUnique(sanitizedPathTemplate, rawPathData, storageSolution);
    }

    private String constructPathUntilUnique(String sanitizedPathTemplate, RawPathData rawPathData,
                                            StorageSolution storageSolution) {
        int suffixIndex = 0;
        String filePath;
        int attemptNumber = 0;
        do {
            attemptNumber++;
            if (attemptNumber >= MAX_ATTEMPTS) {
                throw new CouldNotResolveUniqueFilePathException(rawPathData.gameTitle(),
                        rawPathData.baseName() + rawPathData.extensionWithDot, attemptNumber);
            }

            filePath = constructPath(sanitizedPathTemplate, rawPathData, suffixIndex);
            suffixIndex++;
        } while (storageSolution.fileExists(filePath));

        return filePath;
    }

    private String constructPath(String sanitizedPathTemplate, RawPathData rawPathData, int suffixIndex) {
        String targetFileName = constructFileName(rawPathData, suffixIndex);

        return sanitizedPathTemplate
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
            String baseName = getBaseName(fileName);
            String extensionWithDot = fileName.substring(baseName.length());

            return new RawPathData(
                    fileSource.gameProviderId(),
                    fileSource.originalGameTitle(),
                    baseName,
                    extensionWithDot
            );
        }

        private static String getBaseName(String fileName) {
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex != -1) {
                return fileName.substring(0, dotIndex);
            }
            return fileName;
        }
    }
}
