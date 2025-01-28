package dev.codesoapbox.backity.core.filemanagement.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
public class FilePathProvider {

    private static final Pattern ILLEGAL_CHARACTERS = Pattern.compile("[<>\"|?\n`';!@#$%^&*{}\\[\\]~]");

    final String defaultPathTemplate;
    private final String separator;

    public FilePathProvider(String defaultPathTemplate, FileManager fileManager) {
        this.separator = fileManager.getSeparator();
        this.defaultPathTemplate = replaceWithCorrectFileSeparator(defaultPathTemplate);
    }

    private String replaceWithCorrectFileSeparator(String defaultPathTemplate) {
        return defaultPathTemplate
                .replace("/", separator)
                .replace("\\", separator);
    }

    /**
     * @return the path that was created
     */
    public String createTemporaryFilePath(GameProviderId gameProviderId, String gameTitle) {
        String tempFileName = "TEMP_" + UUID.randomUUID();
        return getFilePath(gameTitle, tempFileName, gameProviderId.value());
    }

    private String getFilePath(String gameTitle, String fileName, String gameProviderId) {
        return defaultPathTemplate
                .replace("{GAME_PROVIDER_ID}", sanitize(gameProviderId))
                .replace("{TITLE}", sanitize(gameTitle))
                .replace("{FILENAME}", sanitize(fileName))
                .replace("\t", " ")
                .replace(":", " -");
    }

    private String sanitize(String value) {
        return ILLEGAL_CHARACTERS.matcher(value).replaceAll("");
    }
}
