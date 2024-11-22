package dev.codesoapbox.backity.core.gamefile.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.gamefile.application.EnqueueFileUseCase;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@GameFileActionsRestResource
@RequiredArgsConstructor
@Slf4j
public class EnqueueFileController {

    private final EnqueueFileUseCase useCase;

    @Operation(summary = "Enqueue file", description = "Adds a discovered file to the download queue")
    @PostMapping("enqueue/{fileVersionId}")
    public ResponseEntity<Void> download(@PathVariable String fileVersionId) {
        GameFileId id = new GameFileId(UUID.fromString(fileVersionId));

        try {
            useCase.enqueue(id);
            return ResponseEntity.ok().build();
        } catch (GameFileNotFoundException e) {
            log.warn("Could not enqueue file.", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
