package dev.codesoapbox.backity.core.shared.domain.services;

public interface MessageService {
    void sendMessage(String topic, Object payload);
}
