package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.controllers.model;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class BackupTargetHttpDtoMapperTest {

    private static final BackupTargetHttpDtoMapper MAPPER = Mappers.getMapper(BackupTargetHttpDtoMapper.class);

    @Test
    void shouldMapDomainToDto() {
        BackupTarget domain = TestBackupTarget.localFolder();

        BackupTargetHttpDto result = MAPPER.toDto(domain);

        BackupTargetHttpDto dto = dto();
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(dto);
    }

    private BackupTargetHttpDto dto() {
        return new BackupTargetHttpDto(
                "eda52c13-ddf7-406f-97d9-d3ce2cab5a76",
                "Local folder",
                "storageSolution1",
                "games/{GAME_PROVIDER_ID}/{TITLE}/{FILENAME}"
        );
    }
}