package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.testing.jpa.DirectJpaPersistenceStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@SuppressWarnings("unused") // Used via component scanning
@RequiredArgsConstructor
public class FileCopyDirectJpaPersistenceStrategy implements DirectJpaPersistenceStrategy<FileCopy, FileCopyJpaEntity> {

    private final FileCopyJpaEntityMapper entityMapper;

    @Override
    public Class<FileCopy> getDomainObjectClass() {
        return FileCopy.class;
    }

    @Override
    public FileCopyJpaEntity toEntity(FileCopy domainObject) {
        return entityMapper.toEntity(domainObject);
    }

    @Override
    public FileCopy toDomain(FileCopyJpaEntity entity) {
        return entityMapper.toDomain(entity);
    }

    @Override
    public FileCopyJpaEntity findPersistedEntity(TestEntityManager entityManager, FileCopy domainObject) {
        return entityManager.find(
                FileCopyJpaEntity.class,
                domainObject.getId().value()
        );
    }
}
