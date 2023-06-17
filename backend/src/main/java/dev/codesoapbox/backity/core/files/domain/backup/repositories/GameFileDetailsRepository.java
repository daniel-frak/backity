package dev.codesoapbox.backity.core.files.domain.backup.repositories;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
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

    Optional<GameFileDetails> findById(GameFileDetailsId id);

    Page<GameFileDetails> findAllDiscovered(Pagination pagination);

    List<GameFileDetails> findAllByGameId(GameId id);
}
