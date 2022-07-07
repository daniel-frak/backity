package dev.codesoapbox.backity.core.logs.domain.services;

import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedMessage;

public interface LogMessageService {

    void sendLogCreated(LogCreatedMessage payload);
}