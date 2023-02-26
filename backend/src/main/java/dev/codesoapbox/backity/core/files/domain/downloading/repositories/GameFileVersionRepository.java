package dev.codesoapbox.backity.core.files.domain.downloading.repositories;

import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GameFileVersionRepository {

    Optional<GameFileVersion> findOldestWaitingForDownload();

    Page<GameFileVersion> findAllWaitingForDownload(Pageable pageable);

    GameFileVersion save(GameFileVersion gameFileVersion);

    Optional<GameFileVersion> findCurrentlyDownloading();

    Page<GameFileVersion> findAllProcessed(Pageable pageable);

    boolean existsByUrlAndVersion(String url, String version);

    Optional<GameFileVersion> findById(Long id);

    Page<GameFileVersion> findAllDiscovered(Pageable pageable);
}
