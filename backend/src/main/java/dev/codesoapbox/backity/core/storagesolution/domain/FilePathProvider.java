package dev.codesoapbox.backity.core.storagesolution.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public class FilePathProvider {

    private static final Pattern ILLEGAL_CHARACTERS = Pattern.compile("[<>\"|?\n`';!@#$%^&*{}\\[\\]~]");

    final String defaultPathTemplate;
    private final StorageSolution storageSolution;

    public FilePathProvider(String defaultPathTemplate, StorageSolution storageSolution) {
        this.storageSolution = storageSolution;
        this.defaultPathTemplate = replaceWithCorrectFileSeparator(defaultPathTemplate);
    }

    private String replaceWithCorrectFileSeparator(String defaultPathTemplate) {
        return defaultPathTemplate
                .replace("/", storageSolution.getSeparator())
                .replace("\\", storageSolution.getSeparator());
    }

    /**
     * @return the path that was created
     */
    public String buildUniqueFilePath(GameProviderId gameProviderId, String gameTitle, String fileName) {
        int suffixIndex = 0;
        String baseName = getBaseName(fileName);
        String extension = fileName.substring(baseName.length());

        String filePath = buildUniqueFilePath(gameProviderId, gameTitle, baseName, extension, suffixIndex);
        while(storageSolution.fileExists(filePath)) {
            suffixIndex++;
            filePath = buildUniqueFilePath(gameProviderId, gameTitle, baseName, extension, suffixIndex);
        }

        return filePath;
    }

    private String buildUniqueFilePath(GameProviderId gameProviderId, String gameTitle, String baseName,
                                       String extension, int suffixIndex) {
        String targetBaseName = baseName;
        if (suffixIndex > 0) {
            targetBaseName = baseName + "_" + suffixIndex;
        }
        String targetFileName = targetBaseName + extension;
        return defaultPathTemplate
                .replace("{GAME_PROVIDER_ID}", sanitize(gameProviderId.value()))
                .replace("{TITLE}", sanitize(gameTitle))
                .replace("{FILENAME}", sanitize(targetFileName))
                .replace("\t", " ")
                .replace(":", " -");
    }

    private String getBaseName(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1) {
            return fileName.substring(0, dotIndex);
        }
        return fileName;
    }

    private String sanitize(String value) {
        return ILLEGAL_CHARACTERS.matcher(value).replaceAll("");
    }
}
