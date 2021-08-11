package dev.codesoapbox.backity.core.logs.application.messages;

import dev.codesoapbox.backity.core.shared.infrastructure.config.openapi.IncludeInOpenApiDocs;
import lombok.Value;

import javax.validation.constraints.NotNull;

@IncludeInOpenApiDocs
@Value(staticConstructor = "of")
public class LogCreatedMessage {

    @NotNull
    String message;

    @NotNull
    Integer maxLogs;
}
