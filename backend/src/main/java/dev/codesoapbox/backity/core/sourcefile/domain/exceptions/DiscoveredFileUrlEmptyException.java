package dev.codesoapbox.backity.core.sourcefile.domain.exceptions;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class DiscoveredFileUrlEmptyException extends DomainInvariantViolationException {

    public DiscoveredFileUrlEmptyException(GameProviderId id) {
        super("Url was empty for Discovered File with Game Provider id: " + id.value());
    }
}
