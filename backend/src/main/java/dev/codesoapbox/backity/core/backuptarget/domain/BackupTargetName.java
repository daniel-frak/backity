package dev.codesoapbox.backity.core.backuptarget.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import lombok.NonNull;

public record BackupTargetName(@NonNull String value) {

    public BackupTargetName {
        if (value.isBlank()) {
            throw new DomainValueIsEmptyException("Backup target name");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
