package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.gamefile.application.usecases.GetCurrentlyDownloadingFileUseCase;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.gamefile.GameFileHttpDto;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.gamefile.GameFileHttpDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@GameFileRestResource
@RequiredArgsConstructor
public class GetCurrentlyDownloadingFileController {

    private final GetCurrentlyDownloadingFileUseCase useCase;
    private final GameFileHttpDtoMapper gameFileMapper;

    @Operation(summary = "Get currently downloading file", description = "Returns the file currently being downloaded")
    @GetMapping("current")
    public GameFileHttpDto getCurrentlyDownloading() {
        return useCase.findCurrentlyDownloadingFile()
                .map(gameFileMapper::toDto)
                .orElse(null);
    }
}
