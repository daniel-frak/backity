package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.gamefile.application.usecases.EnqueueFileUseCase;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@GameFileActionsRestResource
@RequiredArgsConstructor
@Slf4j
public class EnqueueFileController {

    private final EnqueueFileUseCase useCase;

    @Operation(summary = "Enqueue file", description = "Adds a discovered file to the download queue")
    @PostMapping("enqueue/{gameFileId}")
    public ResponseEntity<Void> enqueueFileBackup(@PathVariable String gameFileId) {
        var id = new GameFileId(gameFileId);

        try {
            useCase.enqueue(id);
            return ResponseEntity.ok().build();
        } catch (GameFileNotFoundException e) {
            log.warn("Could not enqueue file.", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
