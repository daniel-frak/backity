package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.application.usecases.DeleteFileCopyUseCase;
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

    @Operation(summary = "Delete file copy", description = "Deletes an already downloaded file copy")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFileCopy(
            @SuppressWarnings("java:S6856")
            @PathVariable("id") String idValue) {
        FileCopyId id = new FileCopyId(idValue);
        useCase.deleteFileCopy(id);

        return ResponseEntity.noContent().build();
    }
}
