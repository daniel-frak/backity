package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driving.api.http.model.sourcefile.SourceFileHttpDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(name = "SourceFileWithCopies")
public record SourceFileWithCopiesHttpDto(
        @NotNull SourceFileHttpDto sourceFile,
        @NotNull List<@Valid FileCopyWithProgressHttpDto> fileCopiesWithProgress
) {
}
