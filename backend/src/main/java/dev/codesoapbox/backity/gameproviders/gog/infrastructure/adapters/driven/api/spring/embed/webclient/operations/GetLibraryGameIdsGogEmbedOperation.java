package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.exceptions.GameListRequestFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@Slf4j
@RequiredArgsConstructor
public class GetLibraryGameIdsGogEmbedOperation {

    private final WebClient webClientEmbed;
    private final GogAuthService authService;

    public List<String> execute() {
        log.info("Retrieving library game ids...");

        @SuppressWarnings("unchecked")
        List<String> gameIds = webClientEmbed.get()
                .uri("/user/data/games")
                .header(GogEmbedHeaders.AUTHORIZATION, getBearerToken())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .onErrorMap(GameListRequestFailedException::new)
                .map(m -> (List<Integer>) m.get("owned"))
                .map(m -> m.stream().map(String::valueOf).toList())
                .block();

        if (gameIds == null) {
            gameIds = emptyList();
        }

        log.info("Found {} games", gameIds.size());

        return gameIds;
    }

    private String getBearerToken() {
        return "Bearer " + authService.getAccessToken();
    }
}
