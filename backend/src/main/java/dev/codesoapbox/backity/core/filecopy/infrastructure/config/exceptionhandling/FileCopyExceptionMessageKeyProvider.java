package dev.codesoapbox.backity.core.filecopy.infrastructure.config.exceptionhandling;

import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotBackedUpException;
import dev.codesoapbox.backity.shared.infrastructure.config.exceptionhandling.ExceptionMessageKeyProvider;

import java.util.Map;

public class FileCopyExceptionMessageKeyProvider implements ExceptionMessageKeyProvider {

    @SuppressWarnings("java:S6411") // Cannot implement Comparable for Class type
    @Override
    public Map<Class<? extends Throwable>, String> getMessageKeys() {
        return Map.of(
                FileCopyNotBackedUpException.class, "FILE_COPY_NOT_BACKED_UP"
        );
    }
}
