package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services;

import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.backity.core.files.downloading.domain.services.SourceFileDownloader;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.auth.GogAuthService;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.embed.GogEmbedClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class GogFileDownloader implements SourceFileDownloader {

    private final GogEmbedClient gogEmbedClient;
    private final GogAuthService authService;
    private final UrlFileDownloader urlFileDownloader;

    @Getter
    private final String source = "GOG";

    @Override
    public void downloadGameFile(EnqueuedFileDownload enqueuedFileDownload, String tempFilePath) throws IOException {
        urlFileDownloader.downloadGameFile(gogEmbedClient, enqueuedFileDownload.getUrl(), tempFilePath);
    }

    @Override
    public boolean isReady() {
        return authService.isAuthenticated();
    }
}
