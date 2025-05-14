package dev.codesoapbox.backity.core.gamefile.application.usecases;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteFileUseCase {

    private final StorageSolution storageSolution;
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
        storageSolution.deleteIfExists(filePath);
    }
}
