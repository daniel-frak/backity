package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.services.SourceFileBackupService;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.embed.GogEmbedWebClient;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class GogFileBackupService implements SourceFileBackupService {

    private final GogEmbedWebClient gogEmbedClient;
    private final GogAuthService authService;
    private final UrlFileDownloader urlFileDownloader;

    @Getter
    private final String source = "GOG";

    @Override
    public String backUpGameFile(GameFileDetails gameFileDetails, String tempFilePath) throws IOException {
        // @TODO Write test for return value
        return urlFileDownloader.downloadGameFile(gogEmbedClient, gameFileDetails, tempFilePath);
    }

    @Override
    public boolean isReady() {
        return authService.isAuthenticated();
    }
}
