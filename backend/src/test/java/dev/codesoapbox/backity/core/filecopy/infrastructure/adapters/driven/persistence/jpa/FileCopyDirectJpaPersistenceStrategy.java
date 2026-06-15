package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.testing.jpa.DirectJpaPersistenceStrategy;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@SuppressWarnings("unused")
public class FileCopyDirectJpaPersistenceStrategy extends DirectJpaPersistenceStrategy<FileCopy, FileCopyJpaEntity> {

    public FileCopyDirectJpaPersistenceStrategy(
            TestEntityManager entityManager,
            FileCopyJpaEntityMapper entityMapper
    ) {
        super(
                entityManager,
                entityMapper::toEntity,
                entityMapper::toDomain,
                (em, obj) -> em.find(FileCopyJpaEntity.class, obj.getId().value()),
                FileCopy.class
        );
    }
}
