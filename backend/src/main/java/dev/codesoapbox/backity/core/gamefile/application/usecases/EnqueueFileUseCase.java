package dev.codesoapbox.backity.core.gamefile.application.usecases;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnqueueFileUseCase {

    private final GameFileRepository gameFileRepository;

    public void enqueue(GameFileId gameFileId) {
        GameFile gameFile = gameFileRepository.getById(gameFileId);
        gameFile.markAsEnqueued();
        gameFileRepository.save(gameFile);
    }
}
