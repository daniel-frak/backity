package dev.codesoapbox.backity.core.logs.domain.model;

import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import lombok.Value;

import javax.validation.constraints.NotNull;

@IncludeInDocumentation
@Value(staticConstructor = "of")
public class LogCreatedMessage {

    @NotNull
    String message;

    @NotNull
    Integer maxLogs;
}
