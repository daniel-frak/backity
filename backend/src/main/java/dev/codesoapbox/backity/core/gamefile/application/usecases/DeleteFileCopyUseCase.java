package dev.codesoapbox.backity.core.gamefile.application.usecases;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteFileCopyUseCase {

    private final StorageSolution storageSolution;
    private final GameFileRepository gameFileRepository;

    public void deleteFileCopy(GameFileId gameFileId) {
        GameFile gameFile = gameFileRepository.getById(gameFileId);
        gameFile.validateIsBackedUp();

        deleteFileCopy(gameFile);
        gameFile.markAsDiscovered();
        gameFileRepository.save(gameFile);
    }

    private void deleteFileCopy(GameFile gameFile) {
        String filePath = gameFile.getFileCopy().filePath();
        storageSolution.deleteIfExists(filePath);
    }
}
