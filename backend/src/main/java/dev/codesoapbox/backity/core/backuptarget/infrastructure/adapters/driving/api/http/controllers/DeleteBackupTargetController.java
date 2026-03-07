package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.controllers;


import dev.codesoapbox.backity.core.backuptarget.application.DeleteBackupTargetUseCase;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

@BackupTargetsRestResource
@RequiredArgsConstructor
public class DeleteBackupTargetController {

    private final DeleteBackupTargetUseCase useCase;

    @Operation(summary = "Delete Backup Target")
    @DeleteMapping("{idValue}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBackupTarget(@PathVariable String idValue) {
        var id = new BackupTargetId(idValue);
        useCase.deleteBackupTarget(id);
    }
}
