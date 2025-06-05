package dev.codesoapbox.backity.core.backup.infrastructure.adapters.adapters.driven.persistence.inmemory;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryFileCopyReplicationProgressRepository implements FileCopyReplicationProgressRepository {

    private final Map<FileCopyId, FileCopyReplicationProgress> replicationProgressesByFileCopyId =
            new ConcurrentHashMap<>();

    @Override
    public void save(FileCopyReplicationProgress progress) {
        replicationProgressesByFileCopyId.put(progress.fileCopyId(), progress);
    }

    @Override
    public void deleteByFileCopyId(FileCopyId fileCopyId) {
        replicationProgressesByFileCopyId.remove(fileCopyId);
    }

    @Override
    public List<FileCopyReplicationProgress> findAllByFileCopyIdIn(Collection<FileCopyId> fileCopyIds) {
        return fileCopyIds.stream()
                .map(replicationProgressesByFileCopyId::get)
                .filter(Objects::nonNull)
                .toList();
    }
}
