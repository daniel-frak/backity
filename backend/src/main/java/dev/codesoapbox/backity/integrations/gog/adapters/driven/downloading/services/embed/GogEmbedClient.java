package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.embed;

import dev.codesoapbox.backity.core.files.downloading.domain.services.DownloadProgress;
import dev.codesoapbox.backity.core.files.downloading.domain.services.FileSizeAccumulator;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.exceptions.GameDetailsRequestFailedException;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.exceptions.GameDownloadRequestFailedException;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.exceptions.GameListRequestFailedException;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.model.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.model.embed.GameFileDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.model.embed.remote.GogGameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.FileBufferProvider;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.auth.GogAuthService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

// https://gogapidocs.readthedocs.io/en/latest/index.html
@Slf4j
@RequiredArgsConstructor
public class GogEmbedClient implements FileBufferProvider {

    static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String VERSION_UNKNOWN_VALUE = "unknown";

    private final WebClient webClientEmbed;
    private final GogAuthService authService;

    public String getLibrarySize() {
        log.info("Calculating library size...");
        var accumulator = new FileSizeAccumulator();
        List<String> libraryGameIds = getLibraryGameIds();

        libraryGameIds.stream()
                .flatMap(id -> getGameDetails(id).getFiles().stream())
                .map(GameFileDetailsResponse::getSize)
                .forEach(accumulator::add);

        String librarySize = accumulator.toString();

        log.info("Library size: {}", librarySize);

        return librarySize;
    }

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

    public GameDetailsResponse getGameDetails(String gameId) {
        log.debug("Retrieving game details for game #" + gameId + "...");

        GameDetailsResponse details = webClientEmbed.get()
                .uri("/account/gameDetails/" + gameId + ".json")
                .header(HEADER_AUTHORIZATION, getBearerToken())
                .retrieve()
                .bodyToMono(GogGameDetailsResponse.class)
                .onErrorMap(e -> new GameDetailsRequestFailedException(gameId, e))
                .map(this::extractGameDetails)
                .block();

        if(details != null) {
            log.debug("Retrieved game details for game: {} (#{})", details.getTitle(), gameId);
        } else {
            log.error("Could not retrieve game details for gameId: {}", gameId);
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
    public Flux<DataBuffer> getFileBuffer(String gameFileUrl, AtomicReference<String> targetFileName, AtomicLong size,
                                          DownloadProgress progress) {
        return webClientEmbed.get()
                .uri(gameFileUrl)
                .header(HEADER_AUTHORIZATION, getBearerToken())
                .exchangeToFlux(response -> {
                    targetFileName.set(extractFileNameFromUrl(response.headers().header("Final-location").get(0)));
                    size.set(Long.parseLong(
                            response.headers().header("content-length").get(0)));

                    long contentLength = response.headers().contentLength().orElse(-1);
                    progress.startTracking(contentLength);

                    return response
                            .bodyToFlux(DataBuffer.class);
                })
                .onErrorMap(e -> new GameDownloadRequestFailedException(gameFileUrl, e))
                .doOnError(e -> log.info("An error occurred while downloading game file" + gameFileUrl, e));
    }

    private String extractFileNameFromUrl(String url) {
        String fileNameTemp = url.substring(url.lastIndexOf('/') + 1);
        if(fileNameTemp.contains("?")) {
            return fileNameTemp.substring(0, fileNameTemp.indexOf("?"));
        }
        return fileNameTemp;
    }

    @SneakyThrows
    private String decode(String redirectUrl) {
        return URLDecoder.decode(redirectUrl, StandardCharsets.UTF_8.toString());
    }
}