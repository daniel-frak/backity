package dev.codesoapbox.backity.core.gamefiledetails.application;

import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsId;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EnqueueFileUseCase {

    private final GameFileDetailsRepository gameFileDetailsRepository;

    public void enqueue(GameFileDetailsId gameFileDetailsId) {
        GameFileDetails gameFileDetails = gameFileDetailsRepository
                .getById(gameFileDetailsId);

        gameFileDetails.enqueue();
        gameFileDetailsRepository.save(gameFileDetails);
    }
}
