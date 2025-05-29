package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyHttpDto;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile.GameFileHttpDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(name = "GameFileWithCopies")
public record GameFileWithCopiesHttpDto(
        @NotNull GameFileHttpDto gameFile,
        @NotNull List<FileCopyHttpDto> fileCopies
) {
}
