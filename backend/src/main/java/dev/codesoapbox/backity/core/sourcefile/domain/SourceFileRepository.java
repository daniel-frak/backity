package dev.codesoapbox.backity.core.sourcefile.domain;

import dev.codesoapbox.backity.core.game.domain.GameId;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SourceFileRepository {

    SourceFile save(SourceFile sourceFile);

    boolean existsByUrlAndVersion(String url, String version);

    SourceFile getById(SourceFileId id);

    Optional<SourceFile> findById(SourceFileId id);

    List<SourceFile> findAllByGameId(GameId id);

    void deleteById(SourceFileId sourceFileId);

    List<SourceFile> findAllByIdIn(Collection<SourceFileId> ids);
}
