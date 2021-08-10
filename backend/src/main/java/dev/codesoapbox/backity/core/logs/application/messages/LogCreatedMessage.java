package dev.codesoapbox.backity.core.logs.application.messages;

import lombok.Value;

@Value(staticConstructor = "of")
public class LogCreatedMessage {

    String message;
    Integer maxLogs;
}
