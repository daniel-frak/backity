package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.embed;

import dev.codesoapbox.backity.core.files.downloading.domain.services.DownloadProgress;
import dev.codesoapbox.backity.core.files.downloading.domain.services.FileSizeAccumulator;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.FileBufferProvider;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.GameDownloadRequestFailedException;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.GameListRequestFailedException;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameFileDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.remote.GogGameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogEmbedClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

// https://gogapidocs.readthedocs.io/en/latest/index.html
@Slf4j
@RequiredArgsConstructor
public class GogEmbedWebClient implements FileBufferProvider, GogEmbedClient {

    static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String VERSION_UNKNOWN_VALUE = "unknown";

    private final WebClient webClientEmbed;
    private final GogAuthService authService;

    @Override
    public String getLibrarySize() {
        log.info("Calculating library size...");
        var accumulator = new FileSizeAccumulator();
        List<String> libraryGameIds = getLibraryGameIds();

        libraryGameIds.stream()
                .map(this::getGameDetails)
                .filter(Objects::nonNull)
                .flatMap(details -> details.getFiles().stream())
                .map(GameFileDetailsResponse::getSize)
                .forEach(accumulator::add);

        String librarySize = accumulator.toString();

        log.info("Library size: {}", librarySize);

        return librarySize;
    }

    @Override
    public List<String> getLibraryGameIds() {
        log.info("Retrieving library game ids...");

        @SuppressWarnings("unchecked")
        List<String> gameIds = webClientEmbed.get()
                .uri("/user/data/games")
                .header(HEADER_AUTHORIZATION, getBearerToken())
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

    @Override
    public GameDetailsResponse getGameDetails(String gameId) {
        log.debug("Retrieving game details for game #" + gameId + "...");

        AtomicBoolean loggedError = new AtomicBoolean();
        GameDetailsResponse details = webClientEmbed.get()
                .uri("/account/gameDetails/" + gameId + ".json")
                .header(HEADER_AUTHORIZATION, getBearerToken())
                .retrieve()
                .bodyToMono(GogGameDetailsResponse.class)
                .onErrorResume(e -> {
                    log.error("Could not retrieve game details for game id: {}", gameId, e);
                    loggedError.set(true);
                    return Mono.empty();
                })
                .map(this::extractGameDetails)
                .block();

        if (details != null) {
            log.debug("Retrieved game details for game: {} (#{})", details.getTitle(), gameId);
        } else if (!loggedError.get()) {
            log.error("Could not retrieve game details for game id: {}. Response was empty.", gameId);
        }

        return details;
    }

    private GameDetailsResponse extractGameDetails(GogGameDetailsResponse response) {
        GameDetailsResponse details = new GameDetailsResponse();
        details.setTitle(response.getTitle());
        details.setBackgroundImage(response.getBackgroundImage());
        details.setCdKey(response.getCdKey());
        details.setTextInformation(response.getTextInformation());
        details.setFiles(getFileDetails(response));
        details.setChangelog(response.getChangelog());

        return details;
    }

    @SuppressWarnings("unchecked")
    private List<GameFileDetailsResponse> getFileDetails(GogGameDetailsResponse response) {
        if (response.getDownloads() == null) {
            return emptyList();
        }

        return response.getDownloads().stream()
                .filter(d -> d.get(0).equals("English"))
                .map(d -> (Map<String, Object>) d.get((1)))
                .flatMap(d -> ((List<Object>) d.get("windows")).stream())
                .map(d -> (Map<String, Object>) d)
                .map(this::toGameFileDetailsResponse)
                .collect(Collectors.toList());
    }

    private GameFileDetailsResponse toGameFileDetailsResponse(Map<String, Object> d) {
        GameFileDetailsResponse gameFileDetails = new GameFileDetailsResponse();
        gameFileDetails.setManualUrl((String) d.get("manualUrl"));
        gameFileDetails.setName((String) d.get("name"));
        gameFileDetails.setVersion(getVersion((String) d.get("version")));
        gameFileDetails.setSize((String) d.get("size"));
        return gameFileDetails;
    }

    private String getVersion(String version) {
        if (version == null) {
            return VERSION_UNKNOWN_VALUE;
        }
        return version;
    }

    @Override
    public Flux<DataBuffer> getFileBuffer(String gameFileUrl, AtomicReference<String> targetFileName,
                                          DownloadProgress progress) {
        return webClientEmbed.get()
                .uri(gameFileUrl)
                .header(HEADER_AUTHORIZATION, getBearerToken())
                .exchangeToFlux(response -> {
                    verifyResponseIsSuccessful(response, gameFileUrl);

                    targetFileName.set(extractFileNameFromResponse(response));
                    progress.startTracking(extractSizeInBytes(response));

                    return response.bodyToFlux(DataBuffer.class);
                });
    }

    private long extractSizeInBytes(ClientResponse response) {
        return response.headers().contentLength().orElse(-1);
    }

    private String extractFileNameFromResponse(ClientResponse response) {
        String url = response.headers().header("Final-location").get(0);
        return extractFileNameFromUrl(url);
    }

    private void verifyResponseIsSuccessful(ClientResponse response, String gameFileUrl) {
        if (!response.statusCode().is2xxSuccessful()) {
            throw new GameDownloadRequestFailedException(gameFileUrl,
                    "Http status code was: " + response.rawStatusCode());
        }
    }

    private String extractFileNameFromUrl(String url) {
        String fileNameTemp = url.substring(url.lastIndexOf('/') + 1);
        if (fileNameTemp.contains("?")) {
            return fileNameTemp.substring(0, fileNameTemp.indexOf("?"));
        }
        return fileNameTemp;
    }
}