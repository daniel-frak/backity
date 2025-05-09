package dev.codesoapbox.backity.core.logs.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.logs.application.GetLogsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Logs", description = "Concerns logs kept in the application's memory")
@RequiredArgsConstructor
@RestController
@RequestMapping("logs")
public class GetLogsController {

    private final GetLogsUseCase logService;

    @Operation(summary = "List logs", description = "Returns the most recent logs")
    @GetMapping
    public List<String> getLogs() {
        return logService.getLogs();
    }
}
