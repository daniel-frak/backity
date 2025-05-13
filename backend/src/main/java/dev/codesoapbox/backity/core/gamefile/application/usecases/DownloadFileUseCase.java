package dev.codesoapbox.backity.core.gamefile.application.usecases;

import dev.codesoapbox.backity.core.filemanagement.domain.FileSystem;
import dev.codesoapbox.backity.core.filemanagement.domain.FileResource;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import lombok.RequiredArgsConstructor;

import java.io.FileNotFoundException;

@RequiredArgsConstructor
public class DownloadFileUseCase {

    private final GameFileRepository gameFileRepository;
    private final FileSystem fileSystem;

    public FileResource downloadFile(GameFileId gameFileId) throws FileNotFoundException {
        GameFile gameFile = gameFileRepository.getById(gameFileId);
        return fileSystem.getFileResource(gameFile.getFileBackup().getFilePath());
    }
}
