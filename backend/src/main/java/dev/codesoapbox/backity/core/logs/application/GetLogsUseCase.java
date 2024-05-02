package dev.codesoapbox.backity.core.logs.application;

import dev.codesoapbox.backity.core.logs.domain.services.LogService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetLogsUseCase {

    private final LogService logService;

    public List<String> getLogs() {
        return logService.getLogs();
    }
}
