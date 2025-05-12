package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.GogConfigHttpDto;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.GogConfigHttpDtoMapper;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.GetGogConfigUseCase;
import dev.codesoapbox.backity.gameproviders.gog.application.GogConfigInfo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@GogConfigRestResource
@RequiredArgsConstructor
public class GetGogConfigController {

    private final GetGogConfigUseCase useCase;
    private final GogConfigHttpDtoMapper mapper;

    @Operation(summary = "Get GOG configuration",
            description = "Returns GOG configuration information")
    @GetMapping
    public GogConfigHttpDto getGogConfig() {
        GogConfigInfo gogConfig = useCase.getGogConfig();
        return mapper.toDto(gogConfig);
    }
}
