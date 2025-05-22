package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.backuptarget.application.GetBackupTargetsUseCase;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.controllers.model.BackupTargetHttpDto;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.controllers.model.BackupTargetHttpDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@BackupTargetsRestResource
@RequiredArgsConstructor
public class GetBackupTargetsController {

    private final GetBackupTargetsUseCase useCase;
    private final BackupTargetHttpDtoMapper mapper;

    @GetMapping
    public List<BackupTargetHttpDto> getBackupTargets() {
        return useCase.getBackupTargets().stream()
                .map(mapper::toDto)
                .toList();
    }
}
