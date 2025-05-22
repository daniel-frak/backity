package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotFoundException;
import dev.codesoapbox.backity.core.filecopy.application.usecases.EnqueueFileCopyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FileCopyActionsRestResource
@RequiredArgsConstructor
@Slf4j
public class EnqueueFileCopyController {

    private final EnqueueFileCopyUseCase useCase;

    @Operation(summary = "Enqueue file copy", description = "Adds a file copy to the backup queue")
    @PostMapping("enqueue/{fileCopyId}")
    public ResponseEntity<Void> enqueueFileCopy(@PathVariable String fileCopyId) {
        var id = new FileCopyId(fileCopyId);

        try {
            useCase.enqueue(id);
            return ResponseEntity.ok().build();
        } catch (FileCopyNotFoundException e) {
            log.warn("Could not enqueue file copy.", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
