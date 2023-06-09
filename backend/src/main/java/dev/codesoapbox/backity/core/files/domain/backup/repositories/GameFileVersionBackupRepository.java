package dev.codesoapbox.backity.core.files.domain.backup.repositories;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GameFileVersionBackupRepository {

    Optional<GameFileVersionBackup> findOldestWaitingForDownload();

    Page<GameFileVersionBackup> findAllWaitingForDownload(Pageable pageable);

    GameFileVersionBackup save(GameFileVersionBackup gameFileVersionBackup);

    Optional<GameFileVersionBackup> findCurrentlyDownloading();

    Page<GameFileVersionBackup> findAllProcessed(Pageable pageable);

    boolean existsByUrlAndVersion(String url, String version);

    Optional<GameFileVersionBackup> findById(Long id);

    Page<GameFileVersionBackup> findAllDiscovered(Pageable pageable);

    List<GameFileVersionBackup> findAllByGameId(GameId id);
}
