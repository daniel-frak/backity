package dev.codesoapbox.backity.core.gamefiledetails.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.gamefiledetails.application.EnqueueFileUseCase;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsId;
import dev.codesoapbox.backity.core.gamefiledetails.domain.exceptions.GameFileDetailsNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@GameFileDetailsRestResource
@RequiredArgsConstructor
@Slf4j
public class EnqueueFileController {

    private final EnqueueFileUseCase useCase;

    @Operation(summary = "Enqueue file", description = "Adds a discovered file to the download queue")
    @GetMapping("enqueue/{gameFileVersionId}")
    public ResponseEntity<Void> download(@PathVariable String gameFileVersionId) {
        GameFileDetailsId id = new GameFileDetailsId(UUID.fromString(gameFileVersionId));

        try {
            useCase.enqueue(id);
            return ResponseEntity.ok().build();
        } catch (GameFileDetailsNotFoundException e) {
            log.warn("Could not enqueue file.", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
