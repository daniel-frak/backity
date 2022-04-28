package dev.codesoapbox.backity.core.logs.domain.model;

import dev.codesoapbox.backity.core.shared.config.openapi.IncludeInOpenApiDocs;
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
