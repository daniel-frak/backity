package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.backuptarget.application.GetLockedBackupTargetIdsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@LockedBackupTargetsRestResource
@RequiredArgsConstructor
public class GetLockedBackupTargetIdsController {

    private final GetLockedBackupTargetIdsUseCase getLockedBackupTargetIdsUseCase;

    @GetMapping
    public List<String> getLockedBackupTargetIds() {
        return getLockedBackupTargetIdsUseCase.getLockedBackupTargetIds().stream()
                .map(id -> id.value().toString())
                .toList();
    }
}
