package dev.codesoapbox.backity.shared.infrastructure.config.exceptionhandling;

import java.util.Map;

public interface ExceptionMessageKeyProvider {

    @SuppressWarnings("java:S6411") // Cannot implement Comparable for Class type
    Map<Class<? extends Throwable>, String> getMessageKeys();
}
