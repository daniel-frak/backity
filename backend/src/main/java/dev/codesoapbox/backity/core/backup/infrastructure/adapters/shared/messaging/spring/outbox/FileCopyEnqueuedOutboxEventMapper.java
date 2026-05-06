package dev.codesoapbox.backity.core.backup.infrastructure.adapters.shared.messaging.spring.outbox;

import dev.codesoapbox.backity.core.backup.domain.events.FileCopyEnqueuedEvent;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.shared.messaging.spring.outbox.FileCopyValueObjectOutboxDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox.OutboxEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox.SharedOutboxDtoMapperConfig;
import org.mapstruct.Mapper;

@OutboxEventMapper(
        domain = FileCopyEnqueuedEvent.class,
        outbox = FileCopyEnqueuedOutboxEvent.class
)
@Mapper(config = SharedOutboxDtoMapperConfig.class,
        uses = {
                FileCopyValueObjectOutboxDtoMapper.class
        })
public interface FileCopyEnqueuedOutboxEventMapper {

    FileCopyEnqueuedOutboxEvent toOutbox(FileCopyEnqueuedEvent domain);

    FileCopyEnqueuedEvent toDomain(FileCopyEnqueuedOutboxEvent outbox);
}
