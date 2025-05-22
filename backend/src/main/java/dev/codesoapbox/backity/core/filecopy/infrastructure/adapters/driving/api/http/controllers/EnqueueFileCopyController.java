package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.application.usecases.EnqueueFileCopyUseCase;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotFoundException;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers.model.EnqueueFileCopyRequestHttpDto;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FileCopyActionsRestResource
@RequiredArgsConstructor
@Slf4j
public class EnqueueFileCopyController {

    private final EnqueueFileCopyUseCase useCase;

    @Operation(summary = "Enqueue file copy", description = "Adds a file copy to the backup queue")
    @PostMapping("enqueue")
    public ResponseEntity<Void> enqueueFileCopy(@Valid @RequestBody EnqueueFileCopyRequestHttpDto request) {
        var gameFileId = new GameFileId(request.fileCopyNaturalId().gameFileId());
        var backupTargetId = new BackupTargetId(request.fileCopyNaturalId().backupTargetId());
        var fileCopyNaturalId = new FileCopyNaturalId(gameFileId, backupTargetId);

        try {
            useCase.enqueue(fileCopyNaturalId);
            return ResponseEntity.ok().build();
        } catch (FileCopyNotFoundException e) {
            log.warn("Could not enqueue file copy.", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
