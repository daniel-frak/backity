package dev.codesoapbox.backity.core.gamefile.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EnqueueFileUseCase {

    private final GameFileRepository gameFileRepository;

    public void enqueue(GameFileId gameFileId) {
        GameFile gameFile = gameFileRepository
                .getById(gameFileId);

        gameFile.enqueue();
        gameFileRepository.save(gameFile);
    }
}
