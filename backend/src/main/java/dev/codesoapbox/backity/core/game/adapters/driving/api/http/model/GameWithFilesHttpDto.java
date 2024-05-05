package dev.codesoapbox.backity.core.game.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.filedetails.FileDetailsHttpDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "GameWithFiles")
public record GameWithFilesHttpDto(
        String id,
        String title,
        List<FileDetailsHttpDto> files
) {
}
