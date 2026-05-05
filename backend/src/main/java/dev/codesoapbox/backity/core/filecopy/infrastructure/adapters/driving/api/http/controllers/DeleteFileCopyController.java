package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.application.usecases.DeleteFileCopyUseCase;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

@FileCopiesRestResource
@RequiredArgsConstructor
@Slf4j
public class DeleteFileCopyController {

    private final DeleteFileCopyUseCase useCase;

    @Operation(summary = "Delete file copy", description = "Deletes an already replicated file copy")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFileCopy(
            @SuppressWarnings("java:S6856")
            @PathVariable("id") String idValue) {
        var id = new FileCopyId(idValue);
        useCase.execute(id);
    }
}
