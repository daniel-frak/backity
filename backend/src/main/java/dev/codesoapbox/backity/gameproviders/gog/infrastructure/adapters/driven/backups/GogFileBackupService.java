package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.backups;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backup.application.GameProviderFileBackupService;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameProviderFile;
import dev.codesoapbox.backity.gameproviders.gog.application.GogFileProvider;
import dev.codesoapbox.backity.gameproviders.gog.application.TrackableFileStream;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class GogFileBackupService implements GameProviderFileBackupService {

    private final GogFileProvider gogFileProvider;
    private final GogAuthService authService;
    private final UrlFileDownloader urlFileDownloader;

    public GameProviderId getGameProviderId() {
        return new GameProviderId("GOG");
    }

    @Override
    public void backUpFile(GameFile gameFile, DownloadProgress downloadProgress) throws IOException {
        GameProviderFile gameProviderFile = gameFile.getGameProviderFile();
        TrackableFileStream fileStream =
                gogFileProvider.initializeProgressAndStreamFile(gameProviderFile, downloadProgress);
        String filePath = gameFile.getFileBackup().getFilePath();
        urlFileDownloader.downloadFile(fileStream, gameFile, filePath);
    }

    @Override
    public boolean isReady() {
        return authService.isAuthenticated();
    }
}
