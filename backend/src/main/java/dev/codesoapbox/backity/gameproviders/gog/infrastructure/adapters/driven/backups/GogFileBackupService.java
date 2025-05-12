package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.backups;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.BackupProgress;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backup.application.GameProviderFileBackupService;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.gameproviders.gog.application.FileBufferProvider;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class GogFileBackupService implements GameProviderFileBackupService {

    private final FileBufferProvider fileBufferProvider;
    private final GogAuthService authService;
    private final UrlFileDownloader urlFileDownloader;

    public GameProviderId getGameProviderId() {
        return new GameProviderId("GOG");
    }

    @Override
    public void backUpFile(GameFile gameFile, BackupProgress backupProgress) throws IOException {
        String filePath = gameFile.getFileBackup().getFilePath();
        urlFileDownloader.downloadFile(fileBufferProvider, gameFile, filePath, backupProgress);
    }

    @Override
    public boolean isReady() {
        return authService.isAuthenticated();
    }
}
