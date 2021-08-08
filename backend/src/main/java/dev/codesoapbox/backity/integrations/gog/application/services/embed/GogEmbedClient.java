package dev.codesoapbox.backity.integrations.gog.application.services.embed;

import dev.codesoapbox.backity.core.files.downloading.domain.services.FileSizeAccumulator;
import dev.codesoapbox.backity.integrations.gog.application.dto.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.application.dto.embed.GameFileDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.application.dto.embed.remote.GogGameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.application.services.auth.GogAuthService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.Collections.*;

// https://gogapidocs.readthedocs.io/en/latest/index.html
@Slf4j
@Service
public class GogEmbedClient {

    private static final String HEADER_AUTHORIZATION = "Authorization";

    private final WebClient webClientEmbed;
    private final WebClient webClientGeneral;
    private final GogAuthService authService;

    public GogEmbedClient(@Qualifier("gogEmbed") WebClient webClientEmbed,
                          @Qualifier("webClientGeneral") WebClient webClientGeneral,
                          GogAuthService authService) {
        this.webClientEmbed = webClientEmbed;
        this.webClientGeneral = webClientGeneral;
        this.authService = authService;
    }

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

        List<String> gameIds = webClientEmbed.get()
                .uri("/user/data/games")
                .header(HEADER_AUTHORIZATION, getBearerToken())
                .retrieve()
                .bodyToMono(Map.class)
                .map(m -> (Map<String, Object>) m)
                .map(m -> (List<Integer>) m.get("owned"))
                .map(m -> m.stream().map(String::valueOf)
                        .collect(Collectors.toList()))
                .block();

        if(gameIds == null) {
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
                .doOnError(e -> log.info("An error occurred while retrieving details for game #" + gameId, e))
                .onErrorReturn(new GogGameDetailsResponse())
                .map(this::extractGameDetails)
                .block();

        if(details != null) {
            log.debug("Retrieved game details for game: {} (#{})", details.getTitle(), gameId);
        } else {
            log.error("Could not retrieve game details for game id: {}", gameId);
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

    private List<GameFileDetailsResponse> getFileDetails(GogGameDetailsResponse response) {
        if (response.getDownloads() == null) {
            return emptyList();
        }

        return response.getDownloads().stream()
                .filter(d -> d.get(0).equals("English"))
                .map(d -> d.get((1)))
                .map(d -> (Map<String, Object>) d)
                .flatMap(d -> ((List<Object>) d.get("windows")).stream())
                .map(d -> (Map<String, Object>) d)
                .map(d -> {
                    GameFileDetailsResponse gameFileDetails = new GameFileDetailsResponse();
                    gameFileDetails.setManualUrl((String) d.get("manualUrl"));
                    gameFileDetails.setName((String) d.get("name"));
                    gameFileDetails.setVersion(getVersion((String) d.get("version")));
                    gameFileDetails.setSize((String) d.get("size"));
                    return gameFileDetails;
                }).collect(Collectors.toList());
    }

    private String getVersion(String version) {
        if (version == null) {
            return "unknown";
        }
        return version;
    }

    public Flux<DataBuffer> getFileBuffer(String gameFileUrl, AtomicReference<String> newFileName, AtomicLong size) {
        return webClientEmbed.get()
                .uri(gameFileUrl)
                .header(HEADER_AUTHORIZATION, getBearerToken())
                .exchangeToFlux(response -> {
//                    if (response.statusCode().is3xxRedirection()) {
                    String redirectUrl = response.headers().header("Location").get(0);
                    log.info("Redirecting to: " + redirectUrl);

                    return webClientGeneral.get()
                            .uri(decode(redirectUrl))
                            .exchangeToFlux(response2 -> {
                                String redirectUrl2 = response2.headers().header("Location").get(0);
                                log.info("Redirecting to: " + redirectUrl2);
                                newFileName.set(extractFileNameFromUrl(redirectUrl2));
                                return webClientEmbed.get()
                                        .uri(redirectUrl2)
                                        .exchangeToFlux(response3 -> {
                                            size.set(Long.parseLong(
                                                    response3.headers().header("content-length").get(0)));
                                            return response3
                                                    .bodyToFlux(DataBuffer.class);
                                        })
                                        .doOnError(e -> log.info(
                                                "(Final redirect) An error occurred while downloading game file"
                                                        + gameFileUrl, e));
                            });
                })
                .doOnError(e -> log.info("An error occurred while downloading game file" + gameFileUrl, e));
    }

    private String extractFileNameFromUrl(String url) {
        String fileNameTemp = url.substring(url.lastIndexOf('/') + 1);
        return fileNameTemp.substring(0, fileNameTemp.indexOf("?"));
    }

    @SneakyThrows
    private String decode(String redirectUrl) {
        return URLDecoder.decode(redirectUrl, StandardCharsets.UTF_8.toString());
    }
}