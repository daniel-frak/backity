package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface FileCopyRepository {

    FileCopy save(FileCopy fileCopy);

    FileCopy getById(FileCopyId id);

    FileCopy findByNaturalIdOrCreate(FileCopyNaturalId naturalId, Supplier<FileCopy> fileCopyFactory);

    Optional<FileCopy> findOldestEnqueued();

    Page<FileCopy> findAllInProgressOrEnqueued(Pagination pagination);

    List<FileCopy> findAllByGameFileId(GameFileId id);
}
