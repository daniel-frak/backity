package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.testing.jpa.DirectJpaPersistenceStrategy;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@SuppressWarnings("unused")
public class SourceFileDirectJpaPersistenceStrategy
        extends DirectJpaPersistenceStrategy<SourceFile, SourceFileJpaEntity> {

    public SourceFileDirectJpaPersistenceStrategy(
            TestEntityManager entityManager,
            SourceFileJpaEntityMapper entityMapper
    ) {
        super(
                entityManager,
                entityMapper::toEntity,
                entityMapper::toDomain,
                (em, obj) -> em.find(SourceFileJpaEntity.class, obj.getId().value()),
                SourceFile.class
        );
    }
}
