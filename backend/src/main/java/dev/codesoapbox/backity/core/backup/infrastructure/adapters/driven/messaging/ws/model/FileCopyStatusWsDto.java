package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FileCopyStatusWsDto")
public enum FileCopyStatusWsDto {

    DISCOVERED, ENQUEUED, IN_PROGRESS, SUCCESS, FAILED
}
