package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.controllers;


import dev.codesoapbox.backity.core.backuptarget.application.AddBackupTargetCommand;
import dev.codesoapbox.backity.core.backuptarget.application.AddBackupTargetUseCase;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.model.BackupTargetHttpDto;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.model.BackupTargetHttpDtoMapper;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@BackupTargetsRestResource
@RequiredArgsConstructor
public class AddBackupTargetController {

    private final AddBackupTargetUseCase useCase;
    private final BackupTargetHttpDtoMapper backupTargetHttpDtoMapper;

    @Operation(summary = "Add Backup Target")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddBackupTargetHttpResponse addBackupTarget(@Valid @RequestBody AddBackupTargetHttpRequest requestBody) {
        AddBackupTargetCommand command = toCommand(requestBody);

        BackupTarget backupTarget = useCase.addBackupTarget(command);

        return AddBackupTargetHttpResponse.from(backupTarget, backupTargetHttpDtoMapper);
    }

    private AddBackupTargetCommand toCommand(AddBackupTargetHttpRequest requestBody) {
        return new AddBackupTargetCommand(
                requestBody.name(),
                new StorageSolutionId(requestBody.storageSolutionId()),
                requestBody.pathTemplate()
        );
    }

    @Schema(name = "AddBackupTargetRequest")
    public record AddBackupTargetHttpRequest(
            @NotBlank String name,
            @NotBlank String storageSolutionId,
            @NotBlank String pathTemplate
    ) {
    }

    @Schema(name = "AddBackupTargetResponse")
    public record AddBackupTargetHttpResponse(
            BackupTargetHttpDto backupTarget
    ) {

        public static AddBackupTargetHttpResponse from(BackupTarget backupTarget, BackupTargetHttpDtoMapper mapper) {
            return new AddBackupTargetHttpResponse(
                    mapper.toDto(backupTarget)
            );
        }
    }
}
