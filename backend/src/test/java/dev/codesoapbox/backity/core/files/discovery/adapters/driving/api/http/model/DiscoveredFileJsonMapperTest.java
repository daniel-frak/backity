package dev.codesoapbox.backity.core.files.discovery.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFileId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscoveredFileJsonMapperTest {

    @Test
    void shouldMapToJson() {
        var dateCreated = LocalDateTime.parse("2007-12-03T10:15:30");
        var dateModified = LocalDateTime.parse("2008-12-03T10:15:30");
        var url = "someUrl";
        var version = "someVersion";
        var source = "someSource";
        var name = "someName";
        var gameTitle = "someGameTitle";
        var size = "someSize";

        var discoveredFile = new DiscoveredFile();
        discoveredFile.setId(new DiscoveredFileId(url, version));
        discoveredFile.setSource(source);
        discoveredFile.setName(name);
        discoveredFile.setGameTitle(gameTitle);
        discoveredFile.setSize(size);
        discoveredFile.setDateCreated(dateCreated);
        discoveredFile.setDateModified(dateModified);
        discoveredFile.setEnqueued(true);
        discoveredFile.setIgnored(true);

        DiscoveredFileJson result = DiscoveredFileJsonMapper.INSTANCE.toJson(discoveredFile);

        assertEquals(discoveredFile.getUniqueId().toString(), result.getId());
        assertEquals(url, result.getUrl());
        assertEquals(version, result.getVersion());
        assertEquals(source, result.getSource());
        assertEquals(name, result.getName());
        assertEquals(gameTitle, result.getGameTitle());
        assertEquals(size, result.getSize());
        assertEquals(dateCreated, result.getDateCreated());
        assertEquals(dateModified, result.getDateModified());
    }
}