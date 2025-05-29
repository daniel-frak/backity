package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FileCopyStatusWsDto")
public enum FileCopyStatusWsDto {

    TRACKED, ENQUEUED, IN_PROGRESS, STORED_INTEGRITY_UNKNOWN, STORED_INTEGRITY_VERIFIED, FAILED
}
