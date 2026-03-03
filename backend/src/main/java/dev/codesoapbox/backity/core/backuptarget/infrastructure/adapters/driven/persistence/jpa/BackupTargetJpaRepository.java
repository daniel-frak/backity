package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.exceptions.BackupTargetNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BackupTargetJpaRepository implements BackupTargetRepository {

    private static final Sort DEFAULT_SORT = Sort.by("dateCreated").ascending();

    private final BackupTargetSpringRepository springRepository;
    private final BackupTargetJpaEntityMapper entityMapper;

    @Transactional
    @Override
    public void save(BackupTarget backupTarget) {
        BackupTargetJpaEntity entity = entityMapper.toEntity(backupTarget);
        springRepository.save(entity);
    }

    @Override
    public BackupTarget getById(BackupTargetId id) {
        return springRepository.findById(id.value())
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new BackupTargetNotFoundException(id));
    }

    @Override
    public List<BackupTarget> findAll() {
        return springRepository.findAll(DEFAULT_SORT).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public List<BackupTarget> findAllByIdIn(Collection<BackupTargetId> ids) {
        var uuids = ids.stream()
                .map(BackupTargetId::value)
                .toList();

        return springRepository.findAllByIdIn(uuids, DEFAULT_SORT).stream()
                .map(entityMapper::toDomain)
                .toList();
    }
}
