package dev.codesoapbox.gogbackupservice.files.downloading.application.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FilePathProvider {

    private final String defaultPathTemplate;

    public FilePathProvider(@Value("${default-path-template}") String defaultPathTemplate) {
        this.defaultPathTemplate = defaultPathTemplate;
    }

    public String getFilePath(String gameTitle, String fileName, String source) {
        return defaultPathTemplate
                .replace("{SOURCE}", source)
                .replace("{TITLE}", gameTitle)
                .replace("{FILENAME}", fileName);
    }
}
