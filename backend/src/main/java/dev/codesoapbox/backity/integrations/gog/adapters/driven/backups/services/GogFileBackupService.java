package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.BackupProgress;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backup.application.GameProviderFileBackupService;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.embed.GogEmbedWebClient;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class GogFileBackupService implements GameProviderFileBackupService {

    private final GogEmbedWebClient gogEmbedClient;
    private final GogAuthService authService;
    private final UrlFileDownloader urlFileDownloader;

    public GameProviderId getGameProviderId() {
        return new GameProviderId("GOG");
    }

    @Override
    public void backUpFile(GameFile gameFile, BackupProgress backupProgress) throws IOException {
        String filePath = gameFile.getFileBackup().getFilePath();
        urlFileDownloader.downloadFile(gogEmbedClient, gameFile, filePath, backupProgress);
    }

    @Override
    public boolean isReady() {
        return authService.isAuthenticated();
    }
}
