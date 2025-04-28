package dev.codesoapbox.backity.core.gamefile.application.usecases;

import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.core.filemanagement.domain.FileResource;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import lombok.RequiredArgsConstructor;

import java.io.FileNotFoundException;

@RequiredArgsConstructor
public class DownloadFileUseCase {

    private final GameFileRepository gameFileRepository;
    private final FileManager fileManager;

    public FileResource downloadFile(GameFileId gameFileId) throws FileNotFoundException {
        GameFile gameFile = gameFileRepository.getById(gameFileId);
        return fileManager.getFileResource(gameFile.getFileBackup().getFilePath());
    }
}
