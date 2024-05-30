package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryProgressChangedEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveryProgressChangedEventUpdateWsMessageMapperTest {

    private static final FileDiscoveryProgressChangedWsEventMapper MAPPER =
            Mappers.getMapper(FileDiscoveryProgressChangedWsEventMapper.class);

    @Test
    void shouldMapToMessage() {
        var progress = new FileDiscoveryProgressChangedEvent("Test Source", 50, 10);

        FileDiscoveryProgressChangedWsEvent result = MAPPER.toWsEvent(progress);

        FileDiscoveryProgressChangedWsEvent expectedResult = new FileDiscoveryProgressChangedWsEvent(
                "Test Source", 50, 10);
        assertThat(result)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}