package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backup.application.GameProviderFileBackupService;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class GogFileBackupService implements GameProviderFileBackupService {

    private final GogEmbedWebClient gogEmbedWebClient;
    private final GogAuthService authService;
    private final UrlFileDownloader urlFileDownloader;

    public GameProviderId getGameProviderId() {
        return new GameProviderId("GOG");
    }

    @Override
    public void replicateFile(StorageSolution storageSolution, GameFile gameFile, FileCopy fileCopy,
                              DownloadProgress downloadProgress) throws IOException {
        FileSource fileSource = gameFile.getFileSource();
        TrackableFileStream fileStream =
                gogEmbedWebClient.initializeProgressAndStreamFile(fileSource, downloadProgress);
        String filePath = fileCopy.getFilePath();
        urlFileDownloader.downloadFile(storageSolution, fileStream, gameFile, filePath);
    }

    @Override
    public boolean isConnected() {
        return authService.isAuthenticated();
    }
}
