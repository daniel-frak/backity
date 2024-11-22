package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;

import java.util.List;
import java.util.Optional;

public interface GameFileRepository {

    Optional<GameFile> findOldestWaitingForDownload();

    Page<GameFile> findAllWaitingForDownload(Pagination pagination);

    GameFile save(GameFile gameFile);

    Optional<GameFile> findCurrentlyDownloading();

    Page<GameFile> findAllProcessed(Pagination pagination);

    boolean existsByUrlAndVersion(String url, String version);

    GameFile getById(GameFileId id);

    Optional<GameFile> findById(GameFileId id);

    Page<GameFile> findAllDiscovered(Pagination pagination);

    List<GameFile> findAllByGameId(GameId id);
}
