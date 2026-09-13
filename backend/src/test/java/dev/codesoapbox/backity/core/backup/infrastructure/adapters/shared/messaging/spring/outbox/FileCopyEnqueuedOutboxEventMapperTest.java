package dev.codesoapbox.backity.core.backup.infrastructure.adapters.shared.messaging.spring.outbox;

import dev.codesoapbox.backity.core.backup.domain.events.FileCopyEnqueuedEvent;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileCopyEnqueuedOutboxEventMapperTest {

    private static final FileCopyEnqueuedOutboxEventMapper MAPPER =
            Mappers.getMapper(FileCopyEnqueuedOutboxEventMapper.class);
    private static final FileCopyEnqueuedEvent DOMAIN_EVENT =
            new FileCopyEnqueuedEvent(new FileCopyId("3fcb6552-58a3-464b-938e-5b871757f4c1"));
    private static final FileCopyEnqueuedOutboxEvent OUTBOX_EVENT =
            new FileCopyEnqueuedOutboxEvent("3fcb6552-58a3-464b-938e-5b871757f4c1");

    @Test
    void shouldMapToDomain() {
        FileCopyEnqueuedEvent result = MAPPER.toDomain(OUTBOX_EVENT);

        assertThat(result).isEqualTo(DOMAIN_EVENT);
    }

    @Test
    void shouldMapToOutbox() {
        FileCopyEnqueuedOutboxEvent result = MAPPER.toOutbox(DOMAIN_EVENT);

        assertThat(result).isEqualTo(OUTBOX_EVENT);
    }
}