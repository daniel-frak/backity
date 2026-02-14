package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.controllers;


import dev.codesoapbox.backity.core.backuptarget.application.EditBackupTargetCommand;
import dev.codesoapbox.backity.core.backuptarget.application.EditBackupTargetUseCase;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@BackupTargetsRestResource
@RequiredArgsConstructor
public class EditBackupTargetController {

    private final EditBackupTargetUseCase useCase;

    @Operation(summary = "Edit Backup Target")
    @PutMapping(value = "{idValue}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editBackupTarget(
            @PathVariable String idValue,
            @Valid @RequestBody EditBackupTargetHttpRequest requestBody) {
        EditBackupTargetCommand command = toCommand(idValue, requestBody);

        useCase.editBackupTarget(command);
    }

    private EditBackupTargetCommand toCommand(String idValue, EditBackupTargetHttpRequest requestBody) {
        return new EditBackupTargetCommand(
                new BackupTargetId(idValue),
                requestBody.name()
        );
    }

    @Schema(name = "EditBackupTargetRequest")
    public record EditBackupTargetHttpRequest(
            @NotBlank String name
    ) {
    }
}
