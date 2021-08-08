package dev.codesoapbox.backity.logs.presentation;

import dev.codesoapbox.backity.logs.application.LogsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Logs")
@RequiredArgsConstructor
@RestController
@RequestMapping("logs")
public class LogsController {

    private final LogsService logsService;

    @GetMapping
    public List<String> getLogs() {
        return logsService.getLogs();
    }
}
