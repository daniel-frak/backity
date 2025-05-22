package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.application.usecases.GetCurrentlyDownloadingFileCopyUseCase;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers.FileCopiesRestResource;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyWithContextHttpDto;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyWithContextHttpDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@FileCopiesRestResource
@RequiredArgsConstructor
public class GetCurrentlyDownloadingFileController {

    private final GetCurrentlyDownloadingFileCopyUseCase useCase;
    private final FileCopyWithContextHttpDtoMapper fileCopyWithContextMapper;

    @Operation(summary = "Get currently downloading file copy",
            description = "Returns the file copy currently being downloaded")
    @GetMapping("current")
    public FileCopyWithContextHttpDto getCurrentlyDownloading() {
        return useCase.findCurrentlyDownloadingFileCopy()
                .map(fileCopyWithContextMapper::toDto)
                .orElse(null);
    }
}
