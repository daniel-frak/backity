package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class JpaGameFileDetailsDetailsMapperTest {

    private final JpaGameFileDetailsMapper MAPPER = Mappers.getMapper(JpaGameFileDetailsMapper.class);

    @Test
    void shouldMapToEntity() {
        var model = new GameFileDetails(
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

        JpaGameFileDetails result = MAPPER.toEntity(model);

        var expectedResult = new JpaGameFileDetails(
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
        var model = new JpaGameFileDetails(
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

        GameFileDetails result = MAPPER.toModel(model);

        var expectedResult = new GameFileDetails(
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