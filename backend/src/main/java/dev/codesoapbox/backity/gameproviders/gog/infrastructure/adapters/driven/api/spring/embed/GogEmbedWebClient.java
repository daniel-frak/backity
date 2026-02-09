package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed;

import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.discovery.domain.exceptions.FileDiscoveryException;
import dev.codesoapbox.backity.core.sourcefile.domain.FileSize;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogLibraryService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.exceptions.GameBackupRequestFailedException;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.exceptions.GameListRequestFailedException;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.model.remote.GogGameDetailsApiResponse;
import dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring.DataBufferFluxTrackableFileStream;
import dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring.DataBufferFluxTrackableFileStreamFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

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
public class GogEmbedWebClient implements GogLibraryService {

    static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String VERSION_UNKNOWN_VALUE = "unknown";
    private static final String ENGLISH_LANGUAGE_VALUE = "English";
    private static final String WINDOWS_SYSTEM_VALUE = "windows";

    private final WebClient webClientEmbed;
    private final JsonMapper jsonMapper;
    private final GogAuthService authService;
    private final DataBufferFluxTrackableFileStreamFactory dataBufferFluxTrackableFileStreamFactory;
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
                .map(GogFile::size)
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
                .bodyToMono(JsonNode.class)
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
            return details;
        } else if (!loggedError.get()) {
            log.info("Could not retrieve game details for game id: {}. Response was empty. This may happen if " +
                    "this is actually a bundle or if the game was migrated to a different id.", gameId);
        }

        return null;
    }

    private Mono<GogGameDetailsApiResponse> safelyMapToResponseObject(JsonNode body) {
        // GOG sometimes returns an empty array instead of an object
        if (body.isArray() && body.isEmpty()) {
            return Mono.empty();
        }

        try {
            return Mono.just(jsonMapper.treeToValue(body, GogGameDetailsApiResponse.class));
        } catch (JacksonException e) {
            return Mono.error(e);
        }
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

    public DataBufferFluxTrackableFileStream initializeProgressAndStreamFile(
            SourceFile sourceFile, OutputStreamProgressTracker progressTracker) {
        String url = sourceFile.getUrl();
        Flux<DataBuffer> dataBufferFlux = webClientEmbed.get()
                .uri(url)
                .header(HEADER_AUTHORIZATION, getBearerToken())
                .exchangeToFlux(response -> {
                    verifyResponseIsSuccessful(response, url);
                    progressTracker.initializeTracking(extractSizeInBytes(response), clock);
                    return response.bodyToFlux(DataBuffer.class);
                });

        return dataBufferFluxTrackableFileStreamFactory.create(dataBufferFlux, progressTracker);
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