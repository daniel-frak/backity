package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

// @TODO TEST
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BackupTargetJpaRepository implements BackupTargetRepository {

    private final BackupTargetSpringRepository springRepository;
    private final BackupTargetJpaEntityMapper entityMapper;

    @Transactional
    @Override
    public void save(BackupTarget backupTarget) {
        // TODO
    }

    @Override
    public BackupTarget getById(BackupTargetId backupTargetId) {
        // @TODO
        return null;
    }

    @Override
    public List<BackupTarget> findAll() {
        // @TODO
        return List.of();
    }

    @Override
    public List<BackupTarget> findAllByIdIn(Collection<BackupTargetId> ids) {
        // @TODO
        return List.of();
    }
}
