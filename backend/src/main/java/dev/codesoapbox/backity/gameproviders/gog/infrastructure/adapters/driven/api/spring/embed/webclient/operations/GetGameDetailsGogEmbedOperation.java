package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations;

import dev.codesoapbox.backity.core.discovery.domain.exceptions.FileDiscoveryException;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.responses.GogGameDetailsApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Collections.emptyList;

@Slf4j
@RequiredArgsConstructor
public class GetGameDetailsGogEmbedOperation {

    private static final String VERSION_UNKNOWN_VALUE = "unknown";
    private static final String ENGLISH_LANGUAGE_VALUE = "English";
    private static final String WINDOWS_SYSTEM_VALUE = "windows";

    private final WebClient webClientEmbed;
    private final GogAuthService authService;
    private final JsonMapper jsonMapper;

    public Optional<GogGameWithFiles> execute(String gameId) {
        log.debug("Retrieving game details for game #{}...", gameId);

        var loggedError = new AtomicBoolean();
        GogGameDetailsApiResponse detailsResponse = webClientEmbed.get()
                .uri("/account/gameDetails/" + gameId + ".json")
                .header(GogEmbedHeaders.AUTHORIZATION, getBearerToken())
                .retrieve()
                .bodyToMono(String.class)
                .filter(body -> !body.isBlank())
                .flatMap(this::safelyMapToResponseObject)
                .onErrorResume(e -> {
                    log.error("Could not retrieve game details for game id: {}", gameId, e);
                    loggedError.set(true);
                    return Mono.empty();
                })
                .block();

        if (detailsResponse != null) {
            GogGameWithFiles details = extractGameDetails(detailsResponse);
            log.debug("Retrieved game details for game: {} (#{})", details.title(), gameId);
            return Optional.of(details);
        } else if (!loggedError.get()) {
            log.info("Could not retrieve game details for game id: {}. Response was empty. This may happen if " +
                    "this is actually a bundle or if the game was migrated to a different id.", gameId);
        }

        return Optional.empty();
    }

    private Mono<GogGameDetailsApiResponse> safelyMapToResponseObject(String bodyString) {
        JsonNode body = jsonMapper.readTree(bodyString);

        // GOG sometimes returns an empty array instead of an object
        if (body.isArray()) {
            return Mono.empty();
        }

        return Mono.just(jsonMapper.treeToValue(body, GogGameDetailsApiResponse.class));
    }

    private GogGameWithFiles extractGameDetails(GogGameDetailsApiResponse response) {
        return new GogGameWithFiles(
                response.title(),
                response.backgroundImage(),
                response.cdKey(),
                response.textInformation(),
                getFiles(response),
                response.changelog()
        );
    }

    private String getBearerToken() {
        return "Bearer " + authService.getAccessToken();
    }

    @SuppressWarnings("unchecked")
    private List<GogFile> getFiles(GogGameDetailsApiResponse response) {
        if (response.downloads() == null) {
            return emptyList();
        }

        return response.downloads().stream()
                .filter(d -> ENGLISH_LANGUAGE_VALUE.equals(d.getFirst()))
                .map(d -> (Map<String, Object>) d.get((1)))
                .flatMap(d -> ((List<Object>) d.get(WINDOWS_SYSTEM_VALUE)).stream())
                .map(d -> (Map<String, Object>) d)
                .map(this::toSourceFileResponse)
                .toList();
    }


    @SuppressWarnings("java:S2637") // Nulls are handled GogFile
    private GogFile toSourceFileResponse(Map<String, Object> sourceFileResponse) {
        String version = getVersion((String) sourceFileResponse.get("version"));
        String manualUrl = (String) sourceFileResponse.get("manualUrl");
        String name = (String) sourceFileResponse.get("name");
        String size = (String) sourceFileResponse.get("size");
        String fileTitle = getFileTitle(manualUrl);

        return new GogFile(version, manualUrl, name, size, fileTitle);
    }

    private String getFileTitle(String fileUrl) {
        return webClientEmbed.head()
                .uri(fileUrl)
                .header(GogEmbedHeaders.AUTHORIZATION, getBearerToken())
                .exchangeToMono(response -> Mono.just(extractFileTitleFromResponse(response))).block();
    }

    private String extractFileTitleFromResponse(ClientResponse response) {
        String finalLocationUrl = response.headers().header("Final-location").getFirst();
        String fileName = extractFileNameFromUrl(finalLocationUrl);
        if (fileName.isBlank()) {
            throw new FileDiscoveryException("Could not extract file title from response");
        }
        return fileName;
    }

    private String extractFileNameFromUrl(String url) {
        String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);
        String fileNameTemp = decodedUrl.substring(decodedUrl.lastIndexOf('/') + 1);
        if (fileNameTemp.contains("?")) {
            return fileNameTemp.substring(0, fileNameTemp.indexOf("?"));
        }
        return fileNameTemp;
    }

    private String getVersion(String version) {
        if (version == null) {
            return VERSION_UNKNOWN_VALUE;
        }
        return version;
    }
}
