package dev.codesoapbox.backity.integrations.gog.presentation;

import dev.codesoapbox.backity.integrations.gog.application.dto.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.application.services.embed.GogEmbedClient;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "GOG")
@Slf4j
@RestController
@RequestMapping("gog")
@RequiredArgsConstructor
public class GogController {

    private final GogEmbedClient gogEmbedClient;

    @GetMapping("library/size")
    public String getLibrarySize() {
        return gogEmbedClient.getLibrarySize();
    }

    @GetMapping("games/{id}")
    public GameDetailsResponse getGameFileDetails(@PathVariable String id) {
        return gogEmbedClient.getGameDetails(id);
    }
}