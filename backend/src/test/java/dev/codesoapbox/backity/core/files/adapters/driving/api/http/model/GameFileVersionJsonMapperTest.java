package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.downloading.model.DownloadStatus;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameFileVersionJsonMapperTest {

    @Test
    void shouldMapToJson() {
        var domain = new GameFileVersion();
        domain.setId(1L);
        domain.setSource("someSource");
        domain.setUrl("someUrl");
        domain.setName("someName");
        domain.setGameTitle("someGameTitle");
        domain.setVersion("someVersion");
        domain.setSize("someSize");
        domain.setDateCreated(LocalDateTime.parse("2007-12-03T10:15:30"));
        domain.setStatus(DownloadStatus.DOWNLOADED);
        domain.setFailedReason("someFailedReason");

        var result = GameFileVersionJsonMapper.INSTANCE.toJson(domain);

        assertEquals(1L, result.getId());
        assertEquals("someSource", result.getSource());
        assertEquals("someUrl", result.getUrl());
        assertEquals("someName", result.getName());
        assertEquals("someGameTitle", result.getGameTitle());
        assertEquals("someVersion", result.getVersion());
        assertEquals("someSize", result.getSize());
        assertEquals(LocalDateTime.parse("2007-12-03T10:15:30"), result.getDateCreated());
        assertEquals(DownloadStatus.DOWNLOADED, result.getStatus());
        assertEquals("someFailedReason", result.getFailedReason());
    }
}