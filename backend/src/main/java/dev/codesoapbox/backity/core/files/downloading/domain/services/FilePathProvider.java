package dev.codesoapbox.backity.core.files.downloading.domain.services;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class FilePathProvider {

    final String defaultPathTemplate;
    private final FileManager fileManager;

    public FilePathProvider(String defaultPathTemplate, FileManager fileManager) {
        this.defaultPathTemplate = replaceWithCorrectFileSeparator(defaultPathTemplate);
        this.fileManager = fileManager;
    }

    private String replaceWithCorrectFileSeparator(String defaultPathTemplate) {
        return defaultPathTemplate
                .replace("/", File.separator)
                .replace("\\", File.separator);
    }

    public String createTemporaryFilePath(String source, String gameTitle) throws IOException {
        String tempFileName = "TEMP_" + UUID.randomUUID();
        String tempFilePath = getFilePath(gameTitle, tempFileName, source);
        createDirectories(tempFilePath);
        return tempFilePath;
    }

    private String getFilePath(String gameTitle, String fileName, String source) {
        return defaultPathTemplate
                .replace("{SOURCE}", source)
                .replace("{TITLE}", gameTitle)
                .replace("{FILENAME}", fileName)
                // @TODO Replace all illegal chars here
                .replace(":", " -");
    }

    private void createDirectories(String tempFilePath) throws IOException {
        String directoryPath = extractDirectory(tempFilePath);
        if (directoryPath != null) {
            fileManager.createDirectories(directoryPath);
        }
    }

    private String extractDirectory(String path) {
        int indexOfLastSeparator = path.lastIndexOf(File.separator);

        if (indexOfLastSeparator == -1) {
            return null;
        }

        return path.substring(0, indexOfLastSeparator);
    }
}
