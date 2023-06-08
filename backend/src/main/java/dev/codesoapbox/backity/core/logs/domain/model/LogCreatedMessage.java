package dev.codesoapbox.backity.core.logs.domain.model;

import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@IncludeInDocumentation
@Value(staticConstructor = "of")
public class LogCreatedMessage {

    @NotNull
    String message;

    @NotNull
    Integer maxLogs;
}
