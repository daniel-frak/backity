package dev.codesoapbox.backity.core.gamefiledetails.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.gamefiledetails.application.GetCurrentlyDownloadingFileUseCase;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefiledetails.GameFileDetailsHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefiledetails.GameFileDetailsHttpDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@GameFileDetailsRestResource
@RequiredArgsConstructor
public class GetCurrentlyDownloadingFileController {

    private final GetCurrentlyDownloadingFileUseCase useCase;
    private final GameFileDetailsHttpDtoMapper gameFileDetailsMapper;

    @Operation(summary = "Get currently downloading file", description = "Returns the file currently being downloaded")
    @GetMapping("current")
    public GameFileDetailsHttpDto getCurrentlyDownloading() {
        return useCase.findCurrentlyDownloadingFile()
                .map(gameFileDetailsMapper::toDto)
                .orElse(null);
    }
}
