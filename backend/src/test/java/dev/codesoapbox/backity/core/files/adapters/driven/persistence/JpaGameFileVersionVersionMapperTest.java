package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersion;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class JpaGameFileVersionVersionMapperTest {

    private final JpaGameFileVersionMapper MAPPER = Mappers.getMapper(JpaGameFileVersionMapper.class);

    @Test
    void shouldMapToEntity() {
        var model = new GameFileVersion(
                123L,
                "someSource",
                "someUrl",
                "someName",
                "someFileName",
                "someFilePath",
                "someGameTitle",
                "someGameId",
                "someVersion",
                "100 KB",
                LocalDateTime.MIN,
                LocalDateTime.MAX,
                FileBackupStatus.IN_PROGRESS,
                "someFailedReason"
        );

        JpaGameFileVersion result = MAPPER.toEntity(model);

        var expectedResult = new JpaGameFileVersion(
                123L,
                "someSource",
                "someUrl",
                "someName",
                "someFileName",
                "someFilePath",
                "someGameTitle",
                "someGameId",
                "someVersion",
                "100 KB",
                LocalDateTime.MIN,
                LocalDateTime.MAX,
                FileBackupStatus.IN_PROGRESS,
                "someFailedReason"
        );

        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void shouldMapToModel() {
        var model = new JpaGameFileVersion(
                123L,
                "someSource",
                "someUrl",
                "someName",
                "someFileName",
                "someFilePath",
                "someGameTitle",
                "someGameId",
                "someVersion",
                "100 KB",
                LocalDateTime.MIN,
                LocalDateTime.MAX,
                FileBackupStatus.IN_PROGRESS,
                "someFailedReason"
        );

        GameFileVersion result = MAPPER.toModel(model);

        var expectedResult = new GameFileVersion(
                123L,
                "someSource",
                "someUrl",
                "someName",
                "someFileName",
                "someFilePath",
                "someGameTitle",
                "someGameId",
                "someVersion",
                "100 KB",
                LocalDateTime.MIN,
                LocalDateTime.MAX,
                FileBackupStatus.IN_PROGRESS,
                "someFailedReason"
        );

        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}