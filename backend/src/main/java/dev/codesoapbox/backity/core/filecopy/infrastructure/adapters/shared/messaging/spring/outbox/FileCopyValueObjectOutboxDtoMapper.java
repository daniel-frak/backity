package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.shared.messaging.spring.outbox;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;

import java.util.UUID;

public class FileCopyValueObjectOutboxDtoMapper {

    public String getValue(FileCopyId id) {
        return id.value().toString();
    }

    public FileCopyId toFileCopyId(UUID uuid) {
        return new FileCopyId(uuid);
    }
}
