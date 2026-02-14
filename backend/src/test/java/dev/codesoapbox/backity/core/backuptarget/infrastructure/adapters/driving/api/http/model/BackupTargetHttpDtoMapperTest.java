package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.PathTemplate;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class BackupTargetHttpDtoMapperTest {

    private static final BackupTargetHttpDtoMapper MAPPER = Mappers.getMapper(BackupTargetHttpDtoMapper.class);

    @Test
    void shouldMapDomainToDto() {
        BackupTarget domain = domainObject();

        BackupTargetHttpDto result = MAPPER.toDto(domain);

        BackupTargetHttpDto dto = dto();
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(dto);
    }

    private BackupTarget domainObject() {
        return TestBackupTarget.localFolderBuilder()
                .withId(new BackupTargetId("eda52c13-ddf7-406f-97d9-d3ce2cab5a76"))
                .withName("Local folder")
                .withStorageSolutionId(new StorageSolutionId("storageSolution1"))
                .withPathTemplate(new PathTemplate("games/{GAME_PROVIDER_ID}/{GAME_TITLE}/{FILENAME}"))
                .build();
    }

    private BackupTargetHttpDto dto() {
        return new BackupTargetHttpDto(
                "eda52c13-ddf7-406f-97d9-d3ce2cab5a76",
                "Local folder",
                "storageSolution1",
                "games/{GAME_PROVIDER_ID}/{GAME_TITLE}/{FILENAME}"
        );
    }
}