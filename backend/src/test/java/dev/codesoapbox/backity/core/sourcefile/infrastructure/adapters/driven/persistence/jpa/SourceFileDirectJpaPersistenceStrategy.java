package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.testing.jpa.DirectJpaPersistenceStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@SuppressWarnings("unused") // Used via component scanning
@RequiredArgsConstructor
public class SourceFileDirectJpaPersistenceStrategy
        implements DirectJpaPersistenceStrategy<SourceFile, SourceFileJpaEntity> {

    private final SourceFileJpaEntityMapper entityMapper;

    @Override
    public Class<SourceFile> getDomainObjectClass() {
        return SourceFile.class;
    }

    @Override
    public SourceFileJpaEntity toEntity(SourceFile domainObject) {
        return entityMapper.toEntity(domainObject);
    }

    @Override
    public SourceFile toDomain(SourceFileJpaEntity entity) {
        return entityMapper.toDomain(entity);
    }

    @Override
    public SourceFileJpaEntity findPersistedEntity(TestEntityManager entityManager, SourceFile domainObject) {
        return entityManager.find(
                SourceFileJpaEntity.class,
                domainObject.getId().value()
        );
    }
}
