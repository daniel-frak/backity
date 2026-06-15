package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.testing.jpa.DirectJpaPersistenceStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@SuppressWarnings("unused") // Used via component scanning
@RequiredArgsConstructor
public class BackupTargetDirectJpaPersistenceStrategy
        implements DirectJpaPersistenceStrategy<BackupTarget, BackupTargetJpaEntity> {

    private final BackupTargetJpaEntityMapper entityMapper;

    @Override
    public Class<BackupTarget> getDomainObjectClass() {
        return BackupTarget.class;
    }

    @Override
    public BackupTargetJpaEntity toEntity(BackupTarget domainObject) {
        return entityMapper.toEntity(domainObject);
    }

    @Override
    public BackupTarget toDomain(BackupTargetJpaEntity entity) {
        return entityMapper.toDomain(entity);
    }

    @Override
    public BackupTargetJpaEntity findPersistedEntity(TestEntityManager entityManager, BackupTarget domainObject) {
        return entityManager.find(
                BackupTargetJpaEntity.class,
                domainObject.getId().value()
        );
    }
}
