package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;

import java.util.Collection;
import java.util.List;

public interface FileCopyReplicationProgressRepository {

    void save(FileCopyReplicationProgress progress);

    void deleteByFileCopyId(FileCopyId fileCopyId);

    List<FileCopyReplicationProgress> findAllByFileCopyIdIn(Collection<FileCopyId> fileCopyIds);
}
