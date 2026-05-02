package dev.codesoapbox.backity.core.backup.infrastructure.adapters.shared.messaging.spring.outbox;

import dev.codesoapbox.backity.core.backup.domain.events.FileCopyEnqueuedEvent;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox.OutboxEventMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@OutboxEventMapper(
        domain = FileCopyEnqueuedEvent.class,
        outbox = FileCopyEnqueuedOutboxEvent.class
)
@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class FileCopyEnqueuedOutboxEventMapper {

    @Mapping(target = "fileCopyId", source = "fileCopyId.value")
    public abstract FileCopyEnqueuedOutboxEvent toOutbox(FileCopyEnqueuedEvent domain);

    public abstract FileCopyEnqueuedEvent toDomain(FileCopyEnqueuedOutboxEvent outbox);

    protected FileCopyId toFileCopyId(String id) {
        return new FileCopyId(id);
    }
}
