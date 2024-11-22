package dev.codesoapbox.backity.core.filemanagement.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class FilePathProvider {

    final String defaultPathTemplate;
    private final FileManager fileManager;
    private final String separator;

    public FilePathProvider(String defaultPathTemplate, FileManager fileManager) {
        this.separator = fileManager.getSeparator();
        this.defaultPathTemplate = replaceWithCorrectFileSeparator(defaultPathTemplate);
        this.fileManager = fileManager;
    }

    private String replaceWithCorrectFileSeparator(String defaultPathTemplate) {
        return defaultPathTemplate
                .replace("/", separator)
                .replace("\\", separator);
    }

    /**
     * @return the path that was created
     */
    public String createTemporaryFilePath(GameProviderId gameProviderId, String gameTitle) throws IOException {
        String tempFileName = "TEMP_" + UUID.randomUUID();
        String tempFilePath = getFilePath(gameTitle, tempFileName, gameProviderId.value());
        createDirectories(tempFilePath);
        return tempFilePath;
    }

    private String getFilePath(String gameTitle, String fileName, String gameProviderId) {
        return defaultPathTemplate
                .replace("{GAME_PROVIDER_ID}", gameProviderId)
                .replace("{TITLE}", gameTitle)
                .replace("{FILENAME}", fileName)
                .replace(":", " -");
    }

    private void createDirectories(String tempFilePath) throws IOException {
        String directoryPath = extractDirectory(tempFilePath);
        if (directoryPath != null) {
            fileManager.createDirectories(directoryPath);
        }
    }

    private String extractDirectory(String path) {
        int indexOfLastSeparator = path.lastIndexOf(separator);

        if (indexOfLastSeparator == -1) {
            return null;
        }

        return path.substring(0, indexOfLastSeparator);
    }
}
