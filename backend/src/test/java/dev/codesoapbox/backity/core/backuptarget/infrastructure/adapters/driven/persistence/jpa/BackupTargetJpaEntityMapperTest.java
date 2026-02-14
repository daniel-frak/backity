package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class BackupTargetJpaEntityMapperTest {

    private static final BackupTargetJpaEntityMapper MAPPER = Mappers.getMapper(BackupTargetJpaEntityMapper.class);

    @Test
    void shouldMapBetweenDomainAndEntity() {
        BackupTarget initialDomainObject = TestBackupTarget.localFolder();

        BackupTargetJpaEntity entity = MAPPER.toEntity(initialDomainObject);
        BackupTarget result = MAPPER.toDomain(entity);

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(initialDomainObject);
    }
}