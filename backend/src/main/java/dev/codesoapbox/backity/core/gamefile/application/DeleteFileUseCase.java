package dev.codesoapbox.backity.core.gamefile.application;

import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteFileUseCase {

    private final FileManager fileManager;
    private final GameFileRepository gameFileRepository;

    public void deleteFile(GameFileId gameFileId) {
        GameFile gameFile = gameFileRepository.getById(gameFileId);
        gameFile.validateIsBackedUp();

        deleteFile(gameFile);
        gameFile.getFileBackup().setStatus(FileBackupStatus.DISCOVERED);
        gameFileRepository.save(gameFile);
    }


    private void deleteFile(GameFile gameFile) {
        String filePath = gameFile.getFileBackup().getFilePath();
        fileManager.deleteIfExists(filePath);
    }
}
