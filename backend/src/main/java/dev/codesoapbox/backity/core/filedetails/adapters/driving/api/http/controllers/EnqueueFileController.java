package dev.codesoapbox.backity.core.filedetails.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filedetails.application.EnqueueFileUseCase;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsId;
import dev.codesoapbox.backity.core.filedetails.domain.exceptions.FileDetailsNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FileDetailsRestResource
@RequiredArgsConstructor
@Slf4j
public class EnqueueFileController {

    private final EnqueueFileUseCase useCase;

    @Operation(summary = "Enqueue file", description = "Adds a discovered file to the download queue")
    @GetMapping("enqueue/{fileVersionId}")
    public ResponseEntity<Void> download(@PathVariable String fileVersionId) {
        FileDetailsId id = new FileDetailsId(UUID.fromString(fileVersionId));

        try {
            useCase.enqueue(id);
            return ResponseEntity.ok().build();
        } catch (FileDetailsNotFoundException e) {
            log.warn("Could not enqueue file.", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
