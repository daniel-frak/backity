package dev.codesoapbox.backity.core.sourcefile.domain;

import dev.codesoapbox.backity.core.sourcefile.domain.exceptions.SourceFileUrlEmptyException;
import lombok.NonNull;
import org.apache.logging.log4j.util.Strings;

public record SourceFileUrl(@NonNull String value) {

    public SourceFileUrl {
        if (Strings.isBlank(value)) {
            throw new SourceFileUrlEmptyException();
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
