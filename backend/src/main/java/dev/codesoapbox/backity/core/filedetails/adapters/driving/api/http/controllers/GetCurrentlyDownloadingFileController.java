package dev.codesoapbox.backity.core.filedetails.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filedetails.application.GetCurrentlyDownloadingFileUseCase;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.filedetails.FileDetailsHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.filedetails.FileDetailsHttpDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@FileDetailsRestResource
@RequiredArgsConstructor
public class GetCurrentlyDownloadingFileController {

    private final GetCurrentlyDownloadingFileUseCase useCase;
    private final FileDetailsHttpDtoMapper fileDetailsMapper;

    @Operation(summary = "Get currently downloading file", description = "Returns the file currently being downloaded")
    @GetMapping("current")
    public FileDetailsHttpDto getCurrentlyDownloading() {
        return useCase.findCurrentlyDownloadingFile()
                .map(fileDetailsMapper::toDto)
                .orElse(null);
    }
}
