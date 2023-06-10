package dev.codesoapbox.backity.core.files.domain.backup.repositories;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GameFileDetailsRepository {

    Optional<GameFileDetails> findOldestWaitingForDownload();

    Page<GameFileDetails> findAllWaitingForDownload(Pageable pageable);

    GameFileDetails save(GameFileDetails gameFileDetails);

    Optional<GameFileDetails> findCurrentlyDownloading();

    Page<GameFileDetails> findAllProcessed(Pageable pageable);

    boolean existsByUrlAndVersion(String url, String version);

    Optional<GameFileDetails> findById(Long id);

    Page<GameFileDetails> findAllDiscovered(Pageable pageable);

    List<GameFileDetails> findAllByGameId(GameId id);
}
