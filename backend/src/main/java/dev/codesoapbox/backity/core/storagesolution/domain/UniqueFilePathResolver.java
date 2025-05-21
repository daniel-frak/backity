package dev.codesoapbox.backity.core.storagesolution.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.util.regex.Pattern;

@Slf4j
public class UniqueFilePathResolver {

    private static final Pattern CHARACTERS_TO_REMOVE = Pattern.compile("[<>\"|?\n`';!@#$%^&*{}\\[\\]~]");

    final String defaultPathTemplate;
    private final StorageSolution storageSolution;

    public UniqueFilePathResolver(String defaultPathTemplate, StorageSolution storageSolution) {
        this.storageSolution = storageSolution;
        this.defaultPathTemplate = replaceWithCorrectFileSeparator(defaultPathTemplate);
    }

    private String replaceWithCorrectFileSeparator(String defaultPathTemplate) {
        return defaultPathTemplate
                .replace("/", storageSolution.getSeparator())
                .replace("\\", storageSolution.getSeparator());
    }

    public String resolve(FileSource fileSource) {
        RawPathData rawPathData = RawPathData.from(fileSource);

        int suffixIndex = 0;
        String filePath = constructPath(rawPathData, suffixIndex);
        while (storageSolution.fileExists(filePath)) {
            suffixIndex++;
            filePath = constructPath(rawPathData, suffixIndex);
        }

        return filePath;
    }

    private String constructPath(RawPathData rawPathData, int suffixIndex) {
        String targetFileName = constructFileName(rawPathData, suffixIndex);

        return defaultPathTemplate
                .replace("{GAME_PROVIDER_ID}", removeIllegalCharacters(rawPathData.gameProviderId().value()))
                .replace("{TITLE}", removeIllegalCharacters(rawPathData.gameTitle()))
                .replace("{FILENAME}", removeIllegalCharacters(targetFileName))
                .replace("\t", " ")
                .replace(":", " -");
    }

    private String constructFileName(RawPathData rawPathData, int suffixIndex) {
        String targetBaseName = (suffixIndex > 0) ? rawPathData.baseName() + "_" + suffixIndex : rawPathData.baseName();
        return targetBaseName + rawPathData.extensionWithDot();
    }

    private String removeIllegalCharacters(String value) {
        return CHARACTERS_TO_REMOVE.matcher(value).replaceAll("");
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
