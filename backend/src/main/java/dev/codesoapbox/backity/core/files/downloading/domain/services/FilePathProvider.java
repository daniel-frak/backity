package dev.codesoapbox.backity.core.files.downloading.domain.services;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class FilePathProvider {

    private final String defaultPathTemplate;
    private final FileManager fileManager;

    public FilePathProvider(String defaultPathTemplate, FileManager fileManager) {
        this.defaultPathTemplate = defaultPathTemplate;
        this.fileManager = fileManager;
    }

    public String createTemporaryFilePath(String source, String gameTitle) throws IOException {
        String tempFileName = "TEMP_" + UUID.randomUUID();
        String tempFilePath = getFilePath(gameTitle, tempFileName, source);
        createDirectories(tempFilePath);
        return tempFilePath;
    }

    public String getFilePath(String gameTitle, String fileName, String source) {
        return defaultPathTemplate
                .replace("{SOURCE}", source)
                .replace("{TITLE}", gameTitle)
                .replace("{FILENAME}", fileName)
                // @TODO Replace all illegal chars here
                .replace(":", " -");
    }

    private void createDirectories(String tempFilePath) throws IOException {
        String directoryPath = extractDirectory(tempFilePath);
        fileManager.createDirectories(directoryPath);
    }

    private String extractDirectory(String path) {
        return path.substring(0, path.lastIndexOf(File.separator));
    }
}
