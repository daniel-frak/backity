package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.backup.domain.FileSourceId;
import dev.codesoapbox.backity.core.backup.domain.SourceFileBackupService;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.embed.GogEmbedWebClient;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class GogFileBackupService implements SourceFileBackupService {

    private final GogEmbedWebClient gogEmbedClient;
    private final GogAuthService authService;
    private final UrlFileDownloader urlFileDownloader;

    public FileSourceId getSource() {
        return new FileSourceId("GOG");
    }

    @Override
    public String backUpGameFile(GameFileDetails gameFileDetails, String tempFilePath) throws IOException {
        return urlFileDownloader.downloadGameFile(gogEmbedClient, gameFileDetails, tempFilePath);
    }

    @Override
    public boolean isReady() {
        return authService.isAuthenticated();
    }
}
