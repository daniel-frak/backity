package dev.codesoapbox.gogbackupservice.gog.presentation;

import dev.codesoapbox.gogbackupservice.gog.application.dto.embed.GameDetailsResponse;
import dev.codesoapbox.gogbackupservice.gog.application.services.embed.GogEmbedClient;
import dev.codesoapbox.gogbackupservice.shared.presentation.ApiControllerPaths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ApiControllerPaths.API + "gog")
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