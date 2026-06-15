package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.testing.jpa.DirectJpaPersistenceStrategy;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@SuppressWarnings("unused")
public class BackupTargetDirectJpaPersistenceStrategy
        extends DirectJpaPersistenceStrategy<BackupTarget, BackupTargetJpaEntity> {

    public BackupTargetDirectJpaPersistenceStrategy(
            TestEntityManager entityManager,
            BackupTargetJpaEntityMapper entityMapper
    ) {
        super(
                entityManager,
                entityMapper::toEntity,
                entityMapper::toDomain,
                (em, obj) -> em.find(BackupTargetJpaEntity.class, obj.getId().value()),
                BackupTarget.class
        );
    }
}
