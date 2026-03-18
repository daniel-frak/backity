package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations;

import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.exceptions.GameBackupRequestFailedException;
import dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring.DataBufferFluxTrackableFileStream;
import dev.codesoapbox.backity.gameproviders.shared.infrastructure.adapters.driven.api.spring.DataBufferFluxTrackableFileStreamFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Clock;

@RequiredArgsConstructor
public class InitializeProgressAndStreamFileGogEmbedOperation {

    private final WebClient webClientEmbed;
    private final GogAuthService authService;
    private final DataBufferFluxTrackableFileStreamFactory dataBufferFluxTrackableFileStreamFactory;
    private final Clock clock;

    public DataBufferFluxTrackableFileStream execute(
            SourceFile sourceFile, OutputStreamProgressTracker progressTracker) {
        String url = sourceFile.getUrl();
        Flux<DataBuffer> dataBufferFlux = webClientEmbed.get()
                .uri(url)
                .header(GogEmbedHeaders.AUTHORIZATION, getBearerToken())
                .exchangeToFlux(response -> {
                    verifyResponseIsSuccessful(response, url);
                    progressTracker.initializeTracking(extractSizeInBytes(response), clock);
                    return response.bodyToFlux(DataBuffer.class);
                });

        return dataBufferFluxTrackableFileStreamFactory.create(dataBufferFlux, progressTracker);
    }

    private String getBearerToken() {
        return "Bearer " + authService.getAccessToken();
    }

    private void verifyResponseIsSuccessful(ClientResponse response, String fileUrl) {
        if (!response.statusCode().is2xxSuccessful()) {
            throw new GameBackupRequestFailedException(fileUrl,
                    "Http status code was: " + response.statusCode().value());
        }
    }

    private long extractSizeInBytes(ClientResponse response) {
        return response.headers().contentLength().orElse(-1);
    }
}
