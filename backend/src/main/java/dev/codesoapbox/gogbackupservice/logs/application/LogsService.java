package dev.codesoapbox.gogbackupservice.logs.application;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class LogsService {

    private final String logPath;

    public LogsService(@Value("${logging.file.name}") String logPath) {
        this.logPath = logPath;
    }

    @SneakyThrows
    public List<String> getLogs() {
        Path path = Paths.get(logPath);
        return Files.readAllLines(path);
    }
}
