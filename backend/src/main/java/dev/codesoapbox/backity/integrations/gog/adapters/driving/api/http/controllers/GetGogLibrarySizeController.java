package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.integrations.gog.application.usecases.GetGogLibrarySizeUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@GogRestResource
@RequiredArgsConstructor
public class GetGogLibrarySizeController {

    private final GetGogLibrarySizeUseCase useCase;

    @Operation(summary = "Get library size", description = "Returns the size of the user's GOG library")
    @GetMapping("library/size")
    public String getLibrarySize() {
        return useCase.getLibrarySize();
    }
}