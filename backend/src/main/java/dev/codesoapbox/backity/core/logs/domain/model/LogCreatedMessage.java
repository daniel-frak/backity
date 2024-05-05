package dev.codesoapbox.backity.core.logs.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class LogCreatedMessage {

    @NotNull
    String message;

    @NotNull
    Integer maxLogs;
}
