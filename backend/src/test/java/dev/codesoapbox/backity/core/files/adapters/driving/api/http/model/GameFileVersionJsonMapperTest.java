package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameFileVersionJsonMapperTest {

    @Test
    void shouldMapToJson() {
        var domain = new GameFileVersion(
                1L, "someSource", "someUrl", "someTitle", "someOriginalFileName",
                "someFilePath", "someGameTitle", "someGameId", "someVersion",
                "100 KB",
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53"), FileBackupStatus.SUCCESS,
                "someFailedReason");

        var result = GameFileVersionJsonMapper.INSTANCE.toJson(domain);

        assertEquals(1L, result.getId());
        assertEquals("someSource", result.getSource());
        assertEquals("someUrl", result.getUrl());
        assertEquals("someTitle", result.getTitle());
        assertEquals("someOriginalFileName", result.getOriginalFileName());
        assertEquals("someGameTitle", result.getGameTitle());
        assertEquals("someVersion", result.getVersion());
        assertEquals("100 KB", result.getSize());
        assertEquals(LocalDateTime.parse("2022-04-29T14:15:53"), result.getDateCreated());
        assertEquals(LocalDateTime.parse("2023-04-29T14:15:53"), result.getDateModified());
        assertEquals(FileBackupStatus.SUCCESS, result.getBackupStatus());
        assertEquals("someFailedReason", result.getBackupFailedReason());
    }
}