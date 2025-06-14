package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.application.usecases.CancelFileCopyUseCase;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FileCopyQueueRestResource
@RequiredArgsConstructor
@Slf4j
public class CancelFileCopyController {

    private final CancelFileCopyUseCase useCase;

    @Operation(summary = "Cancel file copy", description = "Removes a file copy from the backup queue")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> cancelFileCopy(@SuppressWarnings("java:S6856")
                                               @PathVariable("id") String idValue) {
        var fileCopyId = new FileCopyId(idValue);

        try {
            useCase.cancelFileCopy(fileCopyId);
            return ResponseEntity.noContent().build();
        } catch (FileCopyNotFoundException e) {
            log.warn("Could not enqueue file copy.", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
