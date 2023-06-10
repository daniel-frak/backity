package dev.codesoapbox.backity.core.files.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameFileDetailsMessageMapperTest {

    @Test
    void shouldMapToMessage() {
        var domain = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                "someSource", "someUrl", "someTitle", "someOriginalFileName",
                "someFilePath", "someGameTitle", "someGameId", "someVersion",
                "100 KB",
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53"), FileBackupStatus.SUCCESS,
                "someFailedReason");

        var result = GameFileDetailsMessageMapper.INSTANCE.toMessage(domain);

        assertEquals("acde26d7-33c7-42ee-be16-bca91a604b48", result.getId());
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