package dev.codesoapbox.backity.core.gamefiledetails.domain;

import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;

import java.util.List;
import java.util.Optional;

public interface GameFileDetailsRepository {

    Optional<GameFileDetails> findOldestWaitingForDownload();

    Page<GameFileDetails> findAllWaitingForDownload(Pagination pagination);

    GameFileDetails save(GameFileDetails gameFileDetails);

    Optional<GameFileDetails> findCurrentlyDownloading();

    Page<GameFileDetails> findAllProcessed(Pagination pagination);

    boolean existsByUrlAndVersion(String url, String version);

    GameFileDetails getById(GameFileDetailsId id);

    Optional<GameFileDetails> findById(GameFileDetailsId id);

    Page<GameFileDetails> findAllDiscovered(Pagination pagination);

    List<GameFileDetails> findAllByGameId(GameId id);
}
