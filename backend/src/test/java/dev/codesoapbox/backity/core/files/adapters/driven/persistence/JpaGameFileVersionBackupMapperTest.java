package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class JpaGameFileVersionBackupMapperTest {

    private final JpaGameFileVersionBackupMapper MAPPER = Mappers.getMapper(JpaGameFileVersionBackupMapper.class);

    @Test
    void shouldMapToEntity() {
        var model = new GameFileVersionBackup(
                123L,
                "someSource",
                "someUrl",
                "someName",
                "someFileName",
                "someFilePath",
                "someGameTitle",
                "someGameId",
                "someVersion",
                "someSize",
                LocalDateTime.MIN,
                LocalDateTime.MAX,
                FileBackupStatus.IN_PROGRESS,
                "someFailedReason"
        );

        JpaGameFileVersionBackup result = MAPPER.toEntity(model);

        var expectedResult = new JpaGameFileVersionBackup(
                123L,
                "someSource",
                "someUrl",
                "someName",
                "someFileName",
                "someFilePath",
                "someGameTitle",
                "someGameId",
                "someVersion",
                "someSize",
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
        var model = new JpaGameFileVersionBackup(
                123L,
                "someSource",
                "someUrl",
                "someName",
                "someFileName",
                "someFilePath",
                "someGameTitle",
                "someGameId",
                "someVersion",
                "someSize",
                LocalDateTime.MIN,
                LocalDateTime.MAX,
                FileBackupStatus.IN_PROGRESS,
                "someFailedReason"
        );

        GameFileVersionBackup result = MAPPER.toModel(model);

        var expectedResult = new GameFileVersionBackup(
                123L,
                "someSource",
                "someUrl",
                "someName",
                "someFileName",
                "someFilePath",
                "someGameTitle",
                "someGameId",
                "someVersion",
                "someSize",
                LocalDateTime.MIN,
                LocalDateTime.MAX,
                FileBackupStatus.IN_PROGRESS,
                "someFailedReason"
        );

        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}