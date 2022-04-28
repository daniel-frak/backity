package dev.codesoapbox.backity.core.files.downloading.domain.services;

public class FilePathProvider {

    private final String defaultPathTemplate;

    public FilePathProvider(String defaultPathTemplate) {
        this.defaultPathTemplate = defaultPathTemplate;
    }

    public String getFilePath(String gameTitle, String fileName, String source) {
        return defaultPathTemplate
                .replace("{SOURCE}", source)
                .replace("{TITLE}", gameTitle)
                .replace("{FILENAME}", fileName)
                // @TODO Replace all illegal chars here
                .replace(":", " -");
    }
}
