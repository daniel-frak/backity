package dev.codesoapbox.backity.core.gamefile.application.usecases;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.FileResource;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import lombok.RequiredArgsConstructor;

import java.io.FileNotFoundException;

@RequiredArgsConstructor
public class DownloadFileUseCase {

    private final GameFileRepository gameFileRepository;
    private final StorageSolution storageSolution;

    public FileResource downloadFile(GameFileId gameFileId) throws FileNotFoundException {
        GameFile gameFile = gameFileRepository.getById(gameFileId);
        String filePath = gameFile.getFileBackup().filePath();

        return storageSolution.getFileResource(filePath);
    }
}
