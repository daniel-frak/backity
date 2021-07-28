package dev.codesoapbox.gogbackupservice.logs.presentation;

import dev.codesoapbox.gogbackupservice.logs.application.LogsService;
import dev.codesoapbox.gogbackupservice.shared.presentation.ApiControllerPaths;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Tag(name = "Logs")
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiControllerPaths.API + "logs")
public class LogsController {

    private final LogsService logsService;

    @GetMapping
    public List<String> getLogs() {
        return logsService.getLogs();
    }
}
