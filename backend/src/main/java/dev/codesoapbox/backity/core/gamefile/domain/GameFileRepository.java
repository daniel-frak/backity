package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.game.domain.GameId;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GameFileRepository {

    GameFile save(GameFile gameFile);

    boolean existsByUrlAndVersion(String url, String version);

    GameFile getById(GameFileId id);

    Optional<GameFile> findById(GameFileId id);

    List<GameFile> findAllByGameId(GameId id);

    void deleteById(GameFileId gameFileId);

    List<GameFile> findAllByIdIn(Collection<GameFileId> ids);
}
