package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.gamefile.application.usecases.DeleteFileCopyUseCase;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FileCopiesRestResource
@RequiredArgsConstructor
@Slf4j
public class DeleteFileCopyController {

    private final DeleteFileCopyUseCase useCase;

    @Operation(summary = "Delete file backup", description = "Deletes an already downloaded file copy")
    @DeleteMapping
    public ResponseEntity<Void> deleteFileCopy(
            @SuppressWarnings("java:S6856")
            @PathVariable(FileCopiesRestResource.GAME_FILE_ID) String gameFileIdValue) {
        GameFileId id = new GameFileId(gameFileIdValue);
        useCase.deleteFileCopy(id);

        return ResponseEntity.noContent().build();
    }
}
