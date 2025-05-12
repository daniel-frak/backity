package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.library;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.BackupProgress;
import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.exceptions.FileDiscoveryException;
import dev.codesoapbox.backity.gameproviders.gog.domain.exceptions.GameBackupRequestFailedException;
import dev.codesoapbox.backity.gameproviders.gog.domain.exceptions.GameListRequestFailedException;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogLibraryService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.library.model.remote.GogGameDetailsApiResponse;
import dev.codesoapbox.backity.gameproviders.gog.application.FileBufferProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Collections.emptyList;

// https://gogapidocs.readthedocs.io/en/latest/index.html
@Slf4j
@RequiredArgsConstructor
public class GogEmbedWebClient implements GogLibraryService, FileBufferProvider {

    static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String VERSION_UNKNOWN_VALUE = "unknown";
    private static final String ENGLISH_LANGUAGE_VALUE = "English";
    private static final String WINDOWS_SYSTEM_VALUE = "windows";

    private final WebClient webClientEmbed;
    private final GogAuthService authService;
    private final Clock clock;

    @Override
    public String getLibrarySize() {
        log.info("Calculating library size...");
        List<String> libraryGameIds = getLibraryGameIds();
        FileSize accumulatedFileSize = sumFileSizes(libraryGameIds);
        String librarySize = accumulatedFileSize.toString();

        log.info("Library size: {}", librarySize);

        return librarySize;
    }

    private FileSize sumFileSizes(List<String> libraryGameIds) {
        return libraryGameIds.stream()
                .map(this::getGameDetails)
                .filter(Objects::nonNull)
                .flatMap(details -> details.files().stream())
                .map(GogGameFile::size)
                .map(FileSize::fromString)
                .reduce(new FileSize(0L), FileSize::add);
    }

    @Override
    public GogGameWithFiles getGameDetails(String gameId) {
        log.debug("Retrieving game details for game #" + gameId + "...");

        var loggedError = new AtomicBoolean();
        GogGameDetailsApiResponse detailsResponse = webClientEmbed.get()
                .uri("/account/gameDetails/" + gameId + ".json")
                .header(HEADER_AUTHORIZATION, getBearerToken())
                .retrieve()
                .bodyToMono(GogGameDetailsApiResponse.class)
                .onErrorResume(e -> {
                    log.error("Could not retrieve game details for game id: {}", gameId, e);
                    loggedError.set(true);
                    return Mono.empty();
                })
                .block();

        if (detailsResponse != null) {
            GogGameWithFiles details = extractGameDetails(detailsResponse);
            log.debug("Retrieved game details for game: {} (#{})", details.title(), gameId);
            return details;
        } else if (!loggedError.get()) {
            log.error("Could not retrieve game details for game id: {}. Response was empty.", gameId);
        }

        return null;
    }

    private GogGameWithFiles extractGameDetails(GogGameDetailsApiResponse response) {
        return new GogGameWithFiles(
                response.title(),
                response.backgroundImage(),
                response.cdKey(),
                response.textInformation(),
                getGameFiles(response),
                response.changelog()
        );
    }

    @SuppressWarnings("unchecked")
    private List<GogGameFile> getGameFiles(GogGameDetailsApiResponse response) {
        if (response.downloads() == null) {
            return emptyList();
        }

        return response.downloads().stream()
                .filter(d -> ENGLISH_LANGUAGE_VALUE.equals(d.getFirst()))
                .map(d -> (Map<String, Object>) d.get((1)))
                .flatMap(d -> ((List<Object>) d.get(WINDOWS_SYSTEM_VALUE)).stream())
                .map(d -> (Map<String, Object>) d)
                .map(this::toGameFileResponse)
                .toList();
    }

    private GogGameFile toGameFileResponse(Map<String, Object> gameFileResponse) {
        String version = getVersion((String) gameFileResponse.get("version"));
        String manualUrl = (String) gameFileResponse.get("manualUrl");
        String name = (String) gameFileResponse.get("name");
        String size = (String) gameFileResponse.get("size");
        String fileTitle = getFileTitle(manualUrl);

        return new GogGameFile(version, manualUrl, name, size, fileTitle);
    }

    private String getFileTitle(String fileUrl) {
        return webClientEmbed.head()
                .uri(fileUrl)
                .header(HEADER_AUTHORIZATION, getBearerToken())
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
    public Flux<DataBuffer> getFileBuffer(String fileUrl,
                                          BackupProgress progress) {
        return webClientEmbed.get()
                .uri(fileUrl)
                .header(HEADER_AUTHORIZATION, getBearerToken())
                .exchangeToFlux(response -> {
                    verifyResponseIsSuccessful(response, fileUrl);
                    progress.initializeTracking(extractSizeInBytes(response), clock);
                    return response.bodyToFlux(DataBuffer.class);
                });
    }

    private long extractSizeInBytes(ClientResponse response) {
        return response.headers().contentLength().orElse(-1);
    }

    private void verifyResponseIsSuccessful(ClientResponse response, String fileUrl) {
        if (!response.statusCode().is2xxSuccessful()) {
            throw new GameBackupRequestFailedException(fileUrl,
                    "Http status code was: " + response.statusCode().value());
        }
    }
}