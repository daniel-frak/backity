package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model.GogConfigResponseHttpDto;
import dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model.GogConfigResponseHttpDtoMapper;
import dev.codesoapbox.backity.integrations.gog.application.GetGogConfigUseCase;
import dev.codesoapbox.backity.integrations.gog.application.GogConfigInfo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@GogConfigRestResource
@RequiredArgsConstructor
public class GetGogConfigController {

    private final GetGogConfigUseCase useCase;
    private final GogConfigResponseHttpDtoMapper mapper;

    @Operation(summary = "Get GOG configuration",
            description = "Returns GOG configuration information")
    @GetMapping
    public GogConfigResponseHttpDto getGogConfig() {
        GogConfigInfo gogConfig = useCase.getGogConfig();
        return mapper.toDto(gogConfig);
    }
}
