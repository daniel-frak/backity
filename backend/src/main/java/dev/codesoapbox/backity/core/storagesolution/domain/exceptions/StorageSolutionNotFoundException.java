package dev.codesoapbox.backity.core.storagesolution.domain.exceptions;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class StorageSolutionNotFoundException extends DomainInvariantViolationException {

    public StorageSolutionNotFoundException(StorageSolutionId id) {
        super("Could not find " + StorageSolution.class.getSimpleName() + " with id=" + id);
    }
}
