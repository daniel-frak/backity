package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FileCopyStatus")
public enum FileCopyStatusHttpDto {

    TRACKED, ENQUEUED, IN_PROGRESS, STORED_INTEGRITY_UNKNOWN, STORED_INTEGRITY_VERIFIED, FAILED
}
