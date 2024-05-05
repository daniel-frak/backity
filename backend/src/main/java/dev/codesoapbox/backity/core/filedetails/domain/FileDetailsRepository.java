package dev.codesoapbox.backity.core.filedetails.domain;

import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;

import java.util.List;
import java.util.Optional;

public interface FileDetailsRepository {

    Optional<FileDetails> findOldestWaitingForDownload();

    Page<FileDetails> findAllWaitingForDownload(Pagination pagination);

    FileDetails save(FileDetails fileDetails);

    Optional<FileDetails> findCurrentlyDownloading();

    Page<FileDetails> findAllProcessed(Pagination pagination);

    boolean existsByUrlAndVersion(String url, String version);

    FileDetails getById(FileDetailsId id);

    Optional<FileDetails> findById(FileDetailsId id);

    Page<FileDetails> findAllDiscovered(Pagination pagination);

    List<FileDetails> findAllByGameId(GameId id);
}
